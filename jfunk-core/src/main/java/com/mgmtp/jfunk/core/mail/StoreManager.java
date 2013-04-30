/*
 * Copyright (c) 2013 mgm technology partners GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mgmtp.jfunk.core.mail;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.inject.assistedinject.Assisted;
import com.mgmtp.jfunk.core.exception.MailException;

/**
 * @author rnaegele
 * @since 3.1.0
 */
class StoreManager {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final String FOLDER_SEP = "/";

	private final Properties sessionProperties;
	private final String folderName;
	private final MailAccount mailAccount;

	private final MailArchiver mailArchiver;

	static interface Factory {
		StoreManager create(MailAccount mailAccount);
	}

	@Inject
	StoreManager(@StoreSession final Properties sessionProperties, @MailFolder final String folderName,
			final MailArchiver mailArchiver, @Assisted final MailAccount mailAccount) {
		this.sessionProperties = sessionProperties;
		this.folderName = folderName;
		this.mailArchiver = mailArchiver;
		this.mailAccount = mailAccount;
	}

	<T> List<T> fetchMessages(final Function<Message, T> function, final boolean deleteAfterFetch) {
		return fetchMessages(Predicates.<Message>alwaysTrue(), function, deleteAfterFetch);
	}

	<T> List<T> fetchMessages(final Predicate<Message> condition, final Function<Message, T> function,
			final boolean deleteAfterFetch) {
		Folder folder = openFolder();
		try {
			return fetchMessages(folder, condition, function, deleteAfterFetch);
		} finally {
			closeFolder(folder);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> fetchMessages(final Folder folder, final Predicate<Message> condition,
			final Function<Message, T> function, final boolean deleteAfterFetch) {
		List<Message> messages = doFetchMessages(folder, condition);

		for (Message message : messages) {
			mailArchiver.archiveMessage(message);
		}

		List<T> result = function != null
				? FluentIterable.from(messages).transform(function).toList()
				: (List<T>) messages;

		if (deleteAfterFetch) {
			doDeleteMessages(folder, messages);
		}

		return result;
	}

	private List<Message> doFetchMessages(final Folder folder, final Predicate<Message> condition) {
		try {
			log.info("Fetching e-mail messages...");
			return FluentIterable.from(asList(folder.getMessages())).filter(condition).toList();
		} catch (MessagingException e) {
			throw new MailException("Error while retrieving mails from folder " + folder.getName(), e);
		}
	}

	void deleteMessages(final Predicate<Message> predicate) {
		Folder folder = openFolder();
		try {
			deleteMessages(folder, predicate);
		} finally {
			closeFolder(folder);
		}
	}

	void deleteAllMessages() {
		deleteMessages(Predicates.<Message>alwaysTrue());
	}

	private void deleteMessages(final Folder folder, final Predicate<Message> condition) {
		List<Message> messages = doFetchMessages(folder, condition);
		doDeleteMessages(folder, messages);
	}

	private void doDeleteMessages(final Folder folder, final Collection<Message> messages) {
		if (messages.isEmpty()) {
			return;
		}

		try {
			log.info("Flagging {} message(s) for deletion", messages.size());
			for (Message message : messages) {
				message.setFlag(Flag.DELETED, true);
			}
			Message[] expungedMessages = folder.expunge();
			log.info("Expunged {} messages", expungedMessages.length);
		} catch (MessagingException ex) {
			throw new MailException("Error deleting e-mail message");
		}
	}

	private Folder openFolder() {
		Folder folder = null;
		try {
			folder = openStore().getDefaultFolder();
		} catch (MessagingException e) {
			throw new MailException("Could not open default folder", e);
		}

		String[] subFolderNames = getSubFolderNames(folderName);
		for (String subFolderName : subFolderNames) {
			try {
				log.debug("Opening folder " + subFolderName);
				folder = folder.getFolder(subFolderName);
			} catch (MessagingException e) {
				throw new MailException("Could not open folder " + subFolderName, e);
			}
		}
		try {
			folder.open(Folder.READ_WRITE);
		} catch (MessagingException ex) {
			throw new MailException("Error while opening INBOX for read-write-access", ex);
		}
		return folder;
	}

	private void closeFolder(final Folder folder) {
		if (folder != null && folder.isOpen()) {
			Store store = folder.getStore();
			try {
				folder.close(true);
			} catch (MessagingException ex) {
				log.error("Could not close folder: " + folder.getName(), ex);
			}
			try {
				store.close();
			} catch (MessagingException ex) {
				log.error("Could not close mail store", ex);
			}
		}
	}

	private Store openStore() {
		try {
			Session session = Session.getInstance(sessionProperties, mailAccount.getAuthenticator());
			Store store = session.getStore();
			store.connect();
			log.debug("Successfully connected to email store for account {}", mailAccount.getAccountId());
			return store;
		} catch (MessagingException ex) {
			throw new MailException("Error getting store", ex);
		}
	}

	private String[] getSubFolderNames(final String folder) {
		if (folder == null || folder.length() == 0 || folder.indexOf(FOLDER_SEP) < 0) {
			return new String[] { folder };
		}
		return folder.split(FOLDER_SEP);
	}
}
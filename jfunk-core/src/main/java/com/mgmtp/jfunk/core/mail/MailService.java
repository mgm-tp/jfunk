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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.mail.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.core.exception.MailException;
import com.mgmtp.jfunk.core.mail.StoreManager.Factory;

/**
 * <p>
 * Provides access to IMAP(S) or POP3 mailboxes.
 * </p>
 * <p>
 * Mail accounts need to be reserved using {@link MailAccountManager} before they can be used with
 * methods taking an {@code accountReservationKey}.
 * </p>
 * 
 * @author rnaegele
 * @since 3.1.0
 */
@Singleton
public class MailService {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final MailAccountManager mailAccountManager;
	private final Factory storeManagerFactory;
	private final long defaultTimeoutSeconds;
	private final long defaultSleepMillis;

	@Inject
	MailService(final MailAccountManager mailAccountManager, final StoreManager.Factory storeManagerFactory,
			@MailTimeoutSeconds final long defaultTimeoutSeconds, @MailSleepMillis final long defaultSleepMillis) {
		this.mailAccountManager = mailAccountManager;
		this.storeManagerFactory = storeManagerFactory;
		this.defaultTimeoutSeconds = defaultTimeoutSeconds;
		this.defaultSleepMillis = defaultSleepMillis;
	}

	/**
	 * Tries to find a message for the mail account reserved under the specified
	 * {@code accountReservationKey} applying the specified {@code condition} until it times out
	 * using the default timeout ( {@link EmailConstants#MAIL_TIMEOUT_SECONDS} and
	 * {@link EmailConstants#MAIL_SLEEP_MILLIS}).
	 * 
	 * @param accountReservationKey
	 *            the key under which the account has been reserved
	 * @param condition
	 *            the condition a message must meet
	 */
	public MailMessage findMessage(final String accountReservationKey, final Predicate<Message> condition) {
		return findMessage(accountReservationKey, condition, defaultTimeoutSeconds);
	}

	/**
	 * <p>
	 * Tries to find a message for the specified mail account applying the specified
	 * {@code condition} until it times out using the default timeout (
	 * {@link EmailConstants#MAIL_TIMEOUT_SECONDS} and {@link EmailConstants#MAIL_SLEEP_MILLIS}).
	 * </p>
	 * <b>Note:</b><br />
	 * This method uses the specified mail account independently without reservation. If, however,
	 * the specified mail account has been reserved by any thread (including the current one), an
	 * {@link IllegalStateException} is thrown. </p>
	 * 
	 * @param mailAccount
	 *            the mail account
	 * @param condition
	 *            the condition a message must meet
	 */
	public MailMessage findMessage(final MailAccount mailAccount, final Predicate<Message> condition) {
		return findMessage(mailAccount, condition, defaultTimeoutSeconds);
	}

	/**
	 * Tries to find a message for the mail account reserved under the specified
	 * {@code accountReservationKey} applying the specified {@code condition} until it times out
	 * using the specified {@code timeout} and {@link EmailConstants#MAIL_SLEEP_MILLIS}.
	 * 
	 * @param accountReservationKey
	 *            the key under which the account has been reserved
	 * @param condition
	 *            the condition a message must meet
	 * @param timeoutSeconds
	 *            the timeout in seconds
	 */
	public MailMessage findMessage(final String accountReservationKey, final Predicate<Message> condition,
			final long timeoutSeconds) {
		return findMessage(accountReservationKey, condition, timeoutSeconds, defaultSleepMillis);
	}

	/**
	 * <p>
	 * Tries to find a message for the specified mail account applying the specified
	 * {@code condition} until it times out using the specified {@code timeout} and
	 * {@link EmailConstants#MAIL_SLEEP_MILLIS}.
	 * </p>
	 * <b>Note:</b><br />
	 * This method uses the specified mail account independently without reservation. If, however,
	 * the specified mail account has been reserved by any thread (including the current one), an
	 * {@link IllegalStateException} is thrown. </p>
	 * 
	 * @param mailAccount
	 *            the mail account
	 * @param condition
	 *            the condition a message must meet
	 * @param timeoutSeconds
	 *            the timeout in seconds
	 */
	public MailMessage findMessage(final MailAccount mailAccount, final Predicate<Message> condition, final long timeoutSeconds) {
		return findMessage(mailAccount, condition, timeoutSeconds, defaultSleepMillis);
	}

	/**
	 * Tries to find a message for the mail account reserved under the specified
	 * {@code accountReservationKey} applying the specified {@code condition} until it times out
	 * using the specified {@code timeout} and and {@code sleepMillis}.
	 * 
	 * @param accountReservationKey
	 *            the key under which the account has been reserved
	 * @param condition
	 *            the condition a message must meet
	 * @param timeoutSeconds
	 *            the timeout in seconds
	 * @param sleepMillis
	 *            the time in milliseconds to sleep between polls
	 */
	public MailMessage findMessage(final String accountReservationKey, final Predicate<Message> condition,
			final long timeoutSeconds, final long sleepMillis) {
		MailAccount mailAccount = checkNotNull(mailAccountManager.lookupUsedMailAccountForCurrentThread(accountReservationKey),
				"No mail account reserved for current thread under key '%s'", accountReservationKey);
		return findMessage(mailAccount, condition, MessageFunctions.toMailMessage(), timeoutSeconds, sleepMillis, false);
	}

	/**
	 * <p>
	 * Tries to find a message for the specified mail account applying the specified
	 * {@code condition} until it times out using the specified {@code timeout} and and
	 * {@code sleepMillis}.
	 * </p>
	 * <p>
	 * <b>Note:</b><br />
	 * This method uses the specified mail account independently without reservation. If, however,
	 * the specified mail account has been reserved by any thread (including the current one), an
	 * {@link IllegalStateException} is thrown.
	 * </p>
	 * 
	 * @param mailAccount
	 *            the mail account
	 * @param condition
	 *            the condition a message must meet
	 * @param timeoutSeconds
	 *            the timeout in seconds
	 * @param sleepMillis
	 *            the time in milliseconds to sleep between polls
	 */
	public MailMessage findMessage(final MailAccount mailAccount, final Predicate<Message> condition, final long timeoutSeconds,
			final long sleepMillis) {
		return findMessage(mailAccount, condition, MessageFunctions.toMailMessage(), timeoutSeconds, sleepMillis, true);
	}

	/**
	 * Tries to find a message for the mail account reserved under the specified
	 * {@code accountReservationKey} applying the specified {@code condition} until it times out
	 * using the default timeout ({@link EmailConstants#MAIL_TIMEOUT_SECONDS} and
	 * {@link EmailConstants#MAIL_SLEEP_MILLIS}). The {@link Message} object is transformed by the
	 * specified funtion be it is returned.
	 * 
	 * @param accountReservationKey
	 *            the key under which the account has been reserved
	 * @param condition
	 *            the condition a message must meet
	 * @param function
	 *            function used to transform the internal {@link Message} instance
	 * @param <T>
	 *            the type of the return object as transformed by the specified funtion
	 */
	public <T> T findMessage(final String accountReservationKey, final Predicate<Message> condition,
			final Function<Message, T> function) {
		return findMessage(accountReservationKey, condition, function, defaultTimeoutSeconds);
	}

	/**
	 * <p>
	 * Tries to find a message for the specified mail account applying the specified
	 * {@code condition} until it times out using the default timeout (
	 * {@link EmailConstants#MAIL_TIMEOUT_SECONDS} and {@link EmailConstants#MAIL_SLEEP_MILLIS}).
	 * The {@link Message} object is transformed by the specified funtion be it is returned.
	 * </p>
	 * <b>Note:</b><br />
	 * This method uses the specified mail account independently without reservation. If, however,
	 * the specified mail account has been reserved by any thread (including the current one), an
	 * {@link IllegalStateException} is thrown. </p>
	 * 
	 * @param mailAccount
	 *            the mail account
	 * @param condition
	 *            the condition a message must meet
	 * @param function
	 *            function used to transform the internal {@link Message} instance
	 * @param <T>
	 *            the type of the return object as transformed by the specified funtion
	 */
	public <T> T findMessage(final MailAccount mailAccount, final Predicate<Message> condition,
			final Function<Message, T> function) {
		return findMessage(mailAccount, condition, function, defaultTimeoutSeconds);
	}

	/**
	 * Tries to find a message for the mail account reserved under the specified
	 * {@code accountReservationKey} applying the specified {@code condition} until it times out
	 * using the specified {@code timeout} and {@link EmailConstants#MAIL_SLEEP_MILLIS}. The
	 * {@link Message} object is transformed by the specified funtion be it is returned.
	 * 
	 * @param accountReservationKey
	 *            the key under which the account has been reserved
	 * @param condition
	 *            the condition a message must meet
	 * @param function
	 *            function used to transform the internal {@link Message} instance
	 * @param timeoutSeconds
	 *            the timeout in seconds
	 * @param <T>
	 *            the type of the return object as transformed by the specified funtion
	 */
	public <T> T findMessage(final String accountReservationKey, final Predicate<Message> condition,
			final Function<Message, T> function, final long timeoutSeconds) {
		return findMessage(accountReservationKey, condition, function, timeoutSeconds, defaultSleepMillis);
	}

	/**
	 * <p>
	 * Tries to find a message for the specified mail account applying the specified
	 * {@code condition} until it times out using the specified {@code timeout} and
	 * {@link EmailConstants#MAIL_SLEEP_MILLIS}. The {@link Message} object is transformed by the
	 * specified funtion be it is returned.
	 * </p>
	 * <b>Note:</b><br />
	 * This method uses the specified mail account independently without reservation. If, however,
	 * the specified mail account has been reserved by any thread (including the current one), an
	 * {@link IllegalStateException} is thrown. </p>
	 * 
	 * @param mailAccount
	 *            the mail account
	 * @param condition
	 *            the condition a message must meet
	 * @param function
	 *            function used to transform the internal {@link Message} instance
	 * @param timeoutSeconds
	 *            the timeout in seconds
	 * @param <T>
	 *            the type of the return object as transformed by the specified funtion
	 */
	public <T> T findMessage(final MailAccount mailAccount, final Predicate<Message> condition,
			final Function<Message, T> function, final long timeoutSeconds) {
		return findMessage(mailAccount, condition, function, timeoutSeconds, defaultSleepMillis);
	}

	/**
	 * Tries to find a message for the mail account reserved under the specified
	 * {@code accountReservationKey} applying the specified {@code condition} until it times out
	 * using the specified {@code timeout} and and {@code sleepMillis}. The {@link Message} object
	 * is transformed by the specified funtion be it is returned.
	 * 
	 * @param accountReservationKey
	 *            the key under which the account has been reserved
	 * @param condition
	 *            the condition a message must meet
	 * @param function
	 *            function used to transform the internal {@link Message} instance
	 * @param timeoutSeconds
	 *            the timeout in seconds
	 * @param sleepMillis
	 *            the time in milliseconds to sleep between polls
	 * @param <T>
	 *            the type of the return object as transformed by the specified funtion
	 */
	public <T> T findMessage(final String accountReservationKey, final Predicate<Message> condition,
			final Function<Message, T> function, final long timeoutSeconds, final long sleepMillis) {
		MailAccount mailAccount = checkNotNull(mailAccountManager.lookupUsedMailAccountForCurrentThread(accountReservationKey),
				"No mail account reserved for current thread under key '%s'", accountReservationKey);
		return findMessage(mailAccount, condition, function, timeoutSeconds, sleepMillis, false);
	}

	/**
	 * <p>
	 * Tries to find a message for the specified mail account applying the specified
	 * {@code condition} until it times out using the specified {@code timeout} and and
	 * {@code sleepMillis}. The {@link Message} object is transformed by the specified funtion be it
	 * is returned.
	 * </p>
	 * <p>
	 * <b>Note:</b><br />
	 * This method uses the specified mail account independently without reservation. If, however,
	 * the specified mail account has been reserved by any thread (including the current one), an
	 * {@link IllegalStateException} is thrown.
	 * </p>
	 * 
	 * @param mailAccount
	 *            the mail account
	 * @param condition
	 *            the condition a message must meet
	 * @param timeoutSeconds
	 *            the timeout in seconds
	 * @param sleepMillis
	 *            the time in milliseconds to sleep between polls
	 * @param function
	 *            function used to transform the internal {@link Message} instance
	 * @param <T>
	 *            the type of the return object as transformed by the specified funtion
	 */
	public <T> T findMessage(final MailAccount mailAccount, final Predicate<Message> condition,
			final Function<Message, T> function, final long timeoutSeconds, final long sleepMillis) {
		return findMessage(mailAccount, condition, function, timeoutSeconds, sleepMillis, true);
	}

	private <T> T findMessage(final MailAccount mailAccount, final Predicate<Message> condition,
			final Function<Message, T> function, final long timeoutSeconds, final long sleepMillis, final boolean checkReserved) {

		if (checkReserved) {
			checkState(!mailAccountManager.isReserved(mailAccount),
					"Cannot use unreserved mail account that has already been reserved: %s", mailAccount);
		}

		log.info("Fetching e-mails [timeoutSeconds={}, sleepMillis={}, condition={}]", timeoutSeconds, sleepMillis, condition);
		long end = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeoutSeconds);

		StoreManager storeManager = storeManagerFactory.create(mailAccount);
		do {
			List<T> messages = storeManager.fetchMessages(condition, function, true);
			if (messages.isEmpty()) {
				try {
					log.info("No matching e-mails found. Sleeping {} ms...", sleepMillis);
					Thread.sleep(sleepMillis);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					throw new JFunkException("Interrupt received.", ex);
				}
			} else {
				int numMessages = messages.size();
				checkState(numMessages == 1, "%s e-mails found matching: %s, but expected only one!", numMessages, condition);
				log.info("Found matching e-mail.");
				return getOnlyElement(messages);
			}
		} while (end > System.currentTimeMillis());

		throw new MailException(String.format("No matching e-mail found [timeoutSeconds=%d, sleepMillis=%d, condition=%s]",
				timeoutSeconds, sleepMillis, condition));
	}

	/**
	 * Deletes all messages from the mail account reserved under the specified
	 * {@code accountReservationKey}.
	 * 
	 * @param accountReservationKey
	 *            the key under which the account has been reserved
	 */
	public void deleteMessages(final String accountReservationKey) {
		MailAccount mailAccount = checkNotNull(mailAccountManager.lookupUsedMailAccountForCurrentThread(accountReservationKey),
				"No mail account reserved for current thread under key '%s'", accountReservationKey);
		deleteMessages(mailAccount, false);
	}

	/**
	 * <p>
	 * Deletes all messages from the specified mail account.
	 * </p>
	 * <p>
	 * <b>Note:</b><br />
	 * This method uses the specified mail account independently without reservation. If, however,
	 * the specified mail account has been reserved by any thread (including the current one), an
	 * {@link IllegalStateException} is thrown.
	 * </p>
	 * 
	 * @param mailAccount
	 *            the mail account
	 */
	public void deleteMessages(final MailAccount mailAccount) {
		deleteMessages(mailAccount, true);
	}

	private void deleteMessages(final MailAccount mailAccount, final boolean checkReserved) {
		if (checkReserved) {
			checkState(!mailAccountManager.isReserved(mailAccount),
					"Cannot use unreserved mail account that has already been reserved: %s", mailAccount);
		}
		storeManagerFactory.create(mailAccount).deleteAllMessages();
	}
}

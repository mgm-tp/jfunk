/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.mail;

import static com.mgmtp.jfunk.common.util.Varargs.va;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_CHECK_MAXIMAL;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_DEBUG;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_IMAPS_FOLDER;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_IMAPS_HOST;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_IMAPS_PORT;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_IMAPS_SOCKET_FACTORY_CLASS;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_IMAPS_SOCKET_FACTORY_FALLBACK;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_IMAPS_SOCKET_FACTORY_PORT;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_IMAP_FOLDER;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_IMAP_HOST;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_IMAP_PORT;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_IMAP_SOCKET_FACTORY_CLASS;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_IMAP_SOCKET_FACTORY_FALLBACK;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_IMAP_SOCKET_FACTORY_PORT;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_POP3_HOST;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_POP3_PORT;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_POP3_SOCKET_FACTORY_CLASS;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_POP3_SOCKET_FACTORY_FALLBACK;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_POP3_SOCKET_FACTORY_PORT;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_PROTOCOL;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_PROTOCOL_IMAP;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_PROTOCOL_IMAPS;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_PROTOCOL_POP3;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_SLEEP;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_SMTP_AUTH;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_SMTP_HOST;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_SMTP_PORT;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_SMTP_STARTTLS_ENABLE;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_TIMEOUT;
import static com.mgmtp.jfunk.core.mail.EmailConstants.MAIL_TRANSPORT_PROTOCOL;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import java.util.regex.Matcher;

import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgmtp.jfunk.common.config.ModuleScoped;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.exception.MailException;

/**
 * Class for e-mail handling. IMAP and POP3 are supported.
 * 
 * @version $Id$
 */
@ModuleScoped
public class EmailParser {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final String FOLDER_SEP = "/";

	private final String protocol;
	private final String folderName;
	private final long timeout;
	private final long sleep;
	private final SessionUtil sessionUtil;
	private final MailAccount mailAccount;
	private final int maxNumberOfCheckedEmails;
	private final Configuration config;

	@Inject
	public EmailParser(final Configuration config, final MailAccount mailAccount) {
		this.mailAccount = mailAccount;

		protocol = config.get(MAIL_PROTOCOL, "imap");
		maxNumberOfCheckedEmails = config.getInteger(MAIL_CHECK_MAXIMAL, 0);
		sleep = Long.parseLong(config.get(MAIL_SLEEP, "5000"));
		timeout = Long.parseLong(config.get(MAIL_TIMEOUT, "300000"));
		this.config = config;

		log.info("Initializing e-mail parser...");

		Properties sessionProps = new Properties(System.getProperties());
		if (protocol.equals(MAIL_PROTOCOL_IMAP)) {
			folderName = config.get(MAIL_IMAP_FOLDER, "INBOX");
			sessionProps.put(MAIL_IMAP_HOST, config.get(MAIL_IMAP_HOST));
			sessionProps.put(MAIL_IMAP_PORT, config.get(MAIL_IMAP_PORT, "110"));
			sessionProps.put(MAIL_IMAP_SOCKET_FACTORY_CLASS,
					config.get(MAIL_IMAP_SOCKET_FACTORY_CLASS, "javax.net.ssl.SSLSocketFactory"));
			sessionProps.put(MAIL_IMAP_SOCKET_FACTORY_FALLBACK, config.get(MAIL_IMAP_SOCKET_FACTORY_FALLBACK, ""));
			sessionProps.put(MAIL_IMAP_SOCKET_FACTORY_PORT, config.get(MAIL_IMAP_SOCKET_FACTORY_PORT, "993"));
		} else if (protocol.equals(MAIL_PROTOCOL_IMAPS)) {
			folderName = config.get(MAIL_IMAPS_FOLDER, "INBOX");
			sessionProps.put(MAIL_IMAPS_HOST, config.get(MAIL_IMAPS_HOST));
			sessionProps.put(MAIL_IMAPS_PORT, config.get(MAIL_IMAPS_PORT, "993"));
			sessionProps.put(MAIL_IMAPS_SOCKET_FACTORY_CLASS,
					config.get(MAIL_IMAPS_SOCKET_FACTORY_CLASS, "javax.net.ssl.SSLSocketFactory"));
			sessionProps.put(MAIL_IMAPS_SOCKET_FACTORY_FALLBACK, config.get(MAIL_IMAPS_SOCKET_FACTORY_FALLBACK, ""));
			sessionProps.put(MAIL_IMAPS_SOCKET_FACTORY_PORT, config.get(MAIL_IMAPS_SOCKET_FACTORY_PORT, "993"));
		} else if (protocol.equals(MAIL_PROTOCOL_POP3)) {
			folderName = null;
			sessionProps.put(MAIL_POP3_HOST, config.get(MAIL_POP3_HOST));
			sessionProps.put(MAIL_POP3_PORT, config.get(MAIL_POP3_PORT));
			// SSL properties are optional
			sessionProps.put(MAIL_POP3_SOCKET_FACTORY_CLASS, config.get(MAIL_POP3_SOCKET_FACTORY_CLASS, ""));
			sessionProps.put(MAIL_POP3_SOCKET_FACTORY_FALLBACK, config.get(MAIL_POP3_SOCKET_FACTORY_FALLBACK, ""));
			sessionProps.put(MAIL_POP3_SOCKET_FACTORY_PORT, config.get(MAIL_POP3_SOCKET_FACTORY_PORT, ""));
		} else {
			throw new IllegalArgumentException("Invalid mail protocol " + protocol);
		}
		sessionProps.put(MAIL_TRANSPORT_PROTOCOL, config.get(MAIL_TRANSPORT_PROTOCOL, "smtp"));
		sessionProps.put(MAIL_SMTP_HOST, config.get(MAIL_SMTP_HOST));
		sessionProps.put(MAIL_SMTP_PORT, config.get(MAIL_SMTP_PORT, "25"));
		sessionProps.put(MAIL_SMTP_AUTH, config.get(MAIL_SMTP_AUTH, "false"));
		sessionProps.put(MAIL_SMTP_STARTTLS_ENABLE, config.get(MAIL_SMTP_STARTTLS_ENABLE, "false"));
		sessionProps.put(MAIL_DEBUG, config.get(MAIL_DEBUG, "false"));

		sessionUtil = new SessionUtil(sessionProps, mailAccount.getAuthenticator());
		if (log.isTraceEnabled()) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			sessionProps.list(pw);

			log.trace("Mail properties");
			log.trace(sw.toString());
		}
	}

	public MailAccount getMailAccount() {
		return mailAccount;
	}

	public void send(final Message msg) throws MailException {
		Transport transport = null;
		try {
			transport = getSession().getTransport();
			transport.connect();
			transport.sendMessage(msg, msg.getAllRecipients());
		} catch (MessagingException e) {
			throw new MailException("Error while sending mail message", e);
		} finally {
			try {
				transport.close();
			} catch (MessagingException ex) {
				// ignored
			}
		}
	}

	/**
	 * Determine mail timeout (three-step approach)
	 * <ol>
	 * <li>{@link BaseMailObject#getTimeout()} is called. If a non-negative value is returned it
	 * will be used as the timeout.</li>
	 * <li>property {@code mail.timeout.[class name]} (with class name = {@link Class#getName()}
	 * from mail class) will be read. If set it will be used as the timeout.</li>
	 * <li>global timeout {@code mail.timeout} will be used</li>
	 * </ol>
	 */
	long getTimeout(final BaseMailObject mail) {
		long to = mail.getTimeout();
		if (to < 0) {
			to = config.getLong(MAIL_TIMEOUT + "." + mail.getClass().getName(), timeout);
		}
		return to;
	}

	/**
	 * Searches for the given mail type object. When found,
	 * {@link BaseMailObject#process(Message[], int)} is called.
	 * 
	 * @return true if the email was found, false otherwise
	 */
	public boolean read(final BaseMailObject mail) throws MailException, MessagingException {
		long theTimeout = getTimeout(mail);
		long start = System.currentTimeMillis();
		long end = theTimeout + start;

		log.info("Searching for email of type '{}' for account '{}', timeout: {}",
				va(mail.getClass().getName(), mailAccount, DurationFormatUtils.formatDuration(theTimeout, "mm:ss") + " min"));
		try {
			do {
				// Open the default folder
				Folder folder = sessionUtil.getFolder();
				Message[] messages;
				try {
					messages = folder.getMessages();
				} catch (MessagingException e) {
					throw new MailException("Error while retrieving mails from folder " + folder.getName(), e);
				}
				boolean found = mail.process(messages, maxNumberOfCheckedEmails);
				sessionUtil.closeFolder(folder);
				if (found) {
					log.info("Found matching email (took {} s)", (System.currentTimeMillis() - start) / 1000);
					return true;
				}
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					// ignore
				}
			} while (end > System.currentTimeMillis());
		} finally {
			sessionUtil.closeStore();
		}
		return false;
	}

	/**
	 * Searches for emails matching the given mail type object. All that are found are deleted.
	 */
	public void deleteAllEmails(final BaseMailObject mail) throws MailException {
		log.info("Trying to delete emails of type {} for account {}", mail.getClass().getName(), mailAccount);
		try {
			Folder folder = sessionUtil.getFolder();
			Message[] messages;
			try {
				messages = folder.getMessages();
			} catch (MessagingException e) {
				throw new MailException("Error while retrieving emails from folder " + folder.getName(), e);
			}
			if (messages.length == 0) {
				log.info("... no messages in folder. Nothing to delete.");
			} else {
				log.info("... {} emails are to be checked.", messages.length);
			}
			int matches = 0;
			int msgNr = 0;
			for (Message msg : messages) {
				msgNr++;
				String subjectString = "";
				try {
					subjectString = msg.getSubject();
				} catch (MessagingException e) {
					throw new MailException("Could not read email subject", e);
				}
				log.info("Checking {}. message", msgNr);
				// Check if subject matches
				Matcher mS = mail.getSubjectPattern().matcher(subjectString);
				if (!mS.matches()) {
					log.info("... subject does not match:\n{}", subjectString);
					continue;
				}
				log.info("... subject matches");
				// Check if body matches
				String bodyString = mail.getBodyString(msg);
				Matcher mB = mail.getBodyPattern().matcher(bodyString);
				if (!mB.matches()) {
					log.info("... body does not match:\n{}", bodyString);
					continue;
				}
				log.info("... body matches");
				// Do further Checks in processMsg
				if (mail.processMsg(msg, subjectString, bodyString)) {
					log.info("... further checks were passed");
					matches++;
					try {
						msg.setFlag(Flags.Flag.DELETED, true);
						log.info(".. one matching email was deleted.");
					} catch (MessagingException e) {
						throw new MailException("Could not set DELETE flag in email", e);
					}
				} else {
					log.info("... further checks were NOT passed. Email does not match.");
				}
			}
			log.info(matches + " emails matched and were deleted.");
			sessionUtil.closeFolder(folder);
		} finally {
			sessionUtil.closeStore();
		}
	}

	/**
	 * All emails in the configured folder are deleted. Only executed when property
	 * {@code mail.startup.deleteAll} is set to {@code true}.
	 */
	public void deleteAllEmails() throws MailException {
		try {
			Folder folder = sessionUtil.getFolder();
			Message[] messages;
			try {
				messages = folder.getMessages();
			} catch (MessagingException e) {
				throw new MailException("Error while retrieving mails from folder " + folder.getName(), e);
			}
			log.info("Deleted {}", messages.length + (messages.length == 1 ? " message" : " messages"));
			try {
				folder.setFlags(messages, new Flags(Flags.Flag.DELETED), true);
			} catch (MessagingException e) {
				throw new MailException("Could not set delete flag", e);
			}
			sessionUtil.closeFolder(folder);
		} finally {
			sessionUtil.closeStore();
		}
	}

	public Session getSession() {
		return sessionUtil.getSession();
	}

	private class SessionUtil {
		private final Session session;
		private Store store;

		public SessionUtil(final Properties properties, final Authenticator fAuth) {
			session = Session.getInstance(properties, fAuth);
			session.setDebug(Boolean.valueOf(properties.getProperty(MAIL_DEBUG)));
		}

		public Session getSession() {
			return session;
		}

		public Folder getFolder() throws MailException {
			Folder folder;

			try {
				folder = getStore().getDefaultFolder();
			} catch (MessagingException e) {
				throw new MailException("Could not open default folder", e);
			}
			if (folder == null) {
				throw new MailException("No default mail folder (root folder)");
			}
			// Folder is configurable for imap only
			if (MAIL_PROTOCOL_IMAP.equals(protocol) || MAIL_PROTOCOL_IMAPS.equals(protocol)) {
				if (StringUtils.isNotEmpty(folderName)) {
					String[] subFolderNames = getSubFolderNames(folderName);
					for (String subFolderName : subFolderNames) {
						try {
							log.debug("Trying to open IMAP folder " + subFolderName);
							folder = folder.getFolder(subFolderName);
							log.debug("Opened folder " + subFolderName);
						} catch (MessagingException e) {
							throw new MailException("Could not open folder " + subFolderName, e);
						}
					}
				} else {
					throw new MailException("No mail folder configured for IMAP");
				}
			} else {
				// For pop folder INBOX has to be opened
				try {
					folder = folder.getFolder("INBOX");
				} catch (MessagingException e) {
					throw new MailException("Could not open folder INBOX", e);
				}
				if (folder == null) {
					throw new MailException("Folder INBOX is not available");
				}
			}
			try {
				folder.open(Folder.READ_WRITE);
			} catch (MessagingException e1) {
				throw new MailException("Error while opening INBOX for read-write-access", e1);
			}
			return folder;
		}

		public void closeFolder(final Folder folder) throws MailException {
			try {
				if (folder.isOpen()) {
					try {
						folder.close(true);
					} catch (MessagingException e) {
						throw new MailException("Could not close folder=" + folder.getName(), e);
					}
				}
			} finally {
				releaseStore();
			}
		}

		public Store getStore() throws MailException {
			if (store == null) {
				try {
					store = session.getStore(protocol);
				} catch (NoSuchProviderException e) {
					throw new MailException("Could not get email store for account " + mailAccount, e);
				}
			}
			if (!store.isConnected()) {
				try {
					store.connect();
				} catch (MessagingException e) {
					throw new MailException("Could not connect to email store for account " + mailAccount, e);
				}
				log.debug("Successfully connected to email store for account {}", mailAccount);
			}
			return store;
		}

		public void releaseStore() throws MailException {
			closeStore();
		}

		/**
		 * Closes the email store. Must be called before quitting the EmailParser.
		 */
		public void closeStore() throws MailException {
			if (store != null && store.isConnected()) {
				try {
					store.close();
				} catch (MessagingException e) {
					throw new MailException("Could not close email store", e);
				}
				log.debug("Successfully closed email store for account {}", mailAccount);
			}
		}

		private String[] getSubFolderNames(final String folder) {
			if (folder == null || folder.length() == 0 || folder.indexOf(FOLDER_SEP) < 0) {
				log.debug("Folder {} does not contain separator {}", folder, FOLDER_SEP);
				return new String[] { folder };
			}
			return folder.split(FOLDER_SEP);
		}
	}
}
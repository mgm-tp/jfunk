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

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Flags;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgmtp.jfunk.core.exception.MailException;

/**
 * Objects of this class represent a certain email type, characterized by a given subject and body
 * pattern.
 * 
 */
public class BaseMailObject {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected final EmailParser emailParser;
	protected final File moduleArchiveDir;

	private final Pattern subjectPattern;
	private final Pattern bodyPattern;

	private long timeout = -1;
	private boolean deleteAfterProcess = true;

	/**
	 * Creates a new BaseMailObject which will search emails matching the the given subject and body
	 * expression.
	 * 
	 * @param subjectPatternString
	 *            subject regex pattern
	 * @param bodyPatternString
	 *            body regex pattern
	 * @param emailParser
	 *            used for accessing the mailbox
	 * @param moduleArchiveDir
	 *            matching emails are copied to the archive directory
	 */
	public BaseMailObject(final String subjectPatternString, final String bodyPatternString, final EmailParser emailParser,
			final File moduleArchiveDir) {
		this.subjectPattern = Pattern.compile(subjectPatternString);
		this.bodyPattern = Pattern.compile(bodyPatternString);
		this.emailParser = emailParser;
		this.moduleArchiveDir = moduleArchiveDir;
		if (moduleArchiveDir == null) {
			log.warn("No archive directory set, emails maybe lost after processing");
		}
	}

	public Pattern getSubjectPattern() {
		return subjectPattern;
	}

	public Pattern getBodyPattern() {
		return bodyPattern;
	}

	/**
	 * Searches for this mail type.
	 * 
	 * @return {@code true} when successful
	 */
	public boolean read() throws MessagingException {
		return emailParser.read(this);
	}

	/**
	 * Timeout for this mail type. It overrides all other timeout settings. See
	 * {@link EmailParser#getTimeout(BaseMailObject)} for details.
	 */
	public final void setTimeout(final long timeout) {
		this.timeout = timeout;
	}

	public final long getTimeout() {
		return timeout;
	}

	public final boolean isDeleteAfterProcess() {
		return deleteAfterProcess;
	}

	/**
	 * When set to {@code true} (which is the default) any matching email will be deleted after
	 * processing. When set to {@code false} the email will not be deleted.
	 */
	protected final void setDeleteAfterProcess(final boolean deleteAfterProcess) {
		this.deleteAfterProcess = deleteAfterProcess;
	}

	/**
	 * Reads the message body.
	 * 
	 * @param msg
	 *            the message to be read
	 * @return the message body
	 */
	public String getBodyString(final Message msg) {
		String bodyString = null;
		try {
			Object content = msg.getContent();
			if (log.isDebugEnabled()) {
				log.debug("Content object is " + content.getClass());
			}
			if (content instanceof MimeMultipart) {
				MimeMultipart mmp = (MimeMultipart) content;
				bodyString = (String) mmp.getBodyPart(0).getContent();
			} else if (content instanceof String) {
				bodyString = (String) msg.getContent();
			} else {
				log.error("Mail content is neither String nor MimeMultipart, but " + content.getClass());
			}
		} catch (MessagingException e) {
			/*
			 * Error while trying to read email body. This could happen when one email account is
			 * used simultaneously.
			 */
			throw new MailException("Could not read email body", e);
		} catch (IOException e) {
			/*
			 * Error while trying to read email body. This could happen when one email account is
			 * used simultaneously.
			 */
			throw new MailException("Could not read email body", e);
		}
		return bodyString;
	}

	/**
	 * Checks all passed messages. If subject and body match, {@link #processSubject(Matcher)} and
	 * {@link #processBody(Matcher)} are called. If an archive dir is set, the matching email is
	 * copied there. The email is deleted if {@link #isDeleteAfterProcess()} is set to {@code true}.
	 * 
	 * @param maxNumberOfCheckedEmails
	 *            maximum number of messages to check
	 * @return {@code true} if one of the emails matched, {@code false otherwise}
	 */
	boolean process(final Message[] messages, final int maxNumberOfCheckedEmails) throws MessagingException {
		int length = messages.length;

		Message[] msgs = maxNumberOfCheckedEmails > 0 && length > maxNumberOfCheckedEmails
				? new Message[maxNumberOfCheckedEmails]
				: new Message[length];

		for (int i = 0; i < msgs.length; i++) {
			msgs[i] = messages[length - i - 1];
		}
		if (messages.length > 0 && log.isDebugEnabled()) {
			log.debug("Scanning list of emails (containing " + messages.length + (messages.length == 1 ? " email" : " emails")
					+ ") for email "
					+ this);
		}
		for (Message msg : msgs) {
			String subjectString = "";
			try {
				subjectString = msg.getSubject();
			} catch (MessagingException e) {
				throw new MailException("Could not read email subject", e);
			}
			log.debug("Checking message with subject=" + subjectString);

			// check whether subject matches and process subject, if it does
			Matcher mS = subjectPattern.matcher(subjectString);
			if (!mS.matches()) {
				if (log.isDebugEnabled()) {
					log.debug("Subject does not match:\n" + subjectString);
				}
				continue;
			}
			if (log.isDebugEnabled()) {
				log.debug("... found subject");
			}
			// check whether body matches and process body, if it does
			String bodyString = getBodyString(msg);
			Matcher mB = bodyPattern.matcher(bodyString);
			if (!mB.matches()) {
				if (log.isDebugEnabled()) {
					log.debug("Body does not match:\n" + bodyString);
				}
				continue;
			}
			if (log.isDebugEnabled()) {
				log.debug("... found body");
			}

			try {
				if (processMsg(msg, subjectString, bodyString)) {
					processSubject(mS);
					processBody(mB);

					// Process found mail
					if (deleteAfterProcess) {
						try {
							msg.setFlag(Flags.Flag.DELETED, true);
						} catch (MessagingException e) {
							throw new MailException("Could not set DELETE flag in email", e);
						}
					}
					return true;
				}
			} finally {
				archiveMailMessage(msg, subjectString, bodyString);
			}
		}

		return false;
	}

	/**
	 * Writes mail to archive directory. The following parts are written:
	 * <ol>
	 * <li>headers</li>
	 * <li>subject</li>
	 * <li>body</li>
	 * </ol>
	 */
	private void archiveMailMessage(final Message msg, final String subjectString, final String bodyString)
			throws MessagingException {
		if (moduleArchiveDir != null) {
			File dir = new File(moduleArchiveDir, "emails");
			if (!dir.mkdir() && !dir.exists()) {
				throw new MailException("Could not create archive dir for emails");
			}

			int index = dir.list().length;
			String mailFileName = getClass().getName().replace(".", "") + "_" + index + ".txt";

			if (log.isDebugEnabled()) {
				log.debug("Saving email as " + mailFileName + " to archive");
			}

			File f = new File(dir, mailFileName);
			if (f.exists()) {
				throw new MailException("Email name " + mailFileName + " is not unique");
			}
			PrintWriter out = null;
			try {
				f.createNewFile();
				out = new PrintWriter(new FileOutputStream(f));
				out.println("###########");
				out.println("# HEADER  #");
				out.println("###########");

				@SuppressWarnings("unchecked")
				Enumeration<Header> headers = msg.getAllHeaders();
				while (headers.hasMoreElements()) {
					Header header = headers.nextElement();
					out.println(header.getName() + "=" + header.getValue());
				}
				out.println();

				out.println("###########");
				out.println("# SUBJECT #");
				out.println("###########");
				out.println(subjectString);
				out.println();

				out.println("###########");
				out.println("# BODY    #");
				out.println("###########");
				out.println(bodyString);
			} catch (IOException e) {
				throw new MailException("Error while archiving email", e);
			} finally {
				closeQuietly(out);
			}
		}
	}

	/**
	 * By overriding this method it is possible to rework the email message.
	 * 
	 * @return default value is {@code true}
	 */
	@SuppressWarnings("unused")
	protected boolean processMsg(final Message msg, final String subjectString, final String bodyString) {
		// nothing to do here in base class
		return true;
	}

	/**
	 * By overriding this method the email body can be evaluated.
	 */
	@SuppressWarnings("unused")
	protected void processBody(final Matcher matcher) {
		// nothing to do here in base class
	}

	/**
	 * By overriding this method the email subject can be evaluated.
	 */
	protected void processSubject(@SuppressWarnings("unused") final Matcher matcher) {
		// nothing to do here in base class
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		tsb.append("subjectPattern", subjectPattern.pattern());
		tsb.append("bodyPattern", bodyPattern.pattern());
		return tsb.toString();
	}
}
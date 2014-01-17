/*
 * Copyright (c) 2014 mgm technology partners GmbH
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

import static com.google.common.base.Joiner.on;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgmtp.jfunk.core.exception.MailException;

/**
 * An SMTP client.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
@Singleton
public class SmtpClient {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Provider<Session> sessionProvider;

	/**
	 * @param sessionProvider
	 *            provides the {@link Session} used to send messages
	 */
	@Inject
	SmtpClient(@TransportSession final Provider<Session> sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	/**
	 * Sends the specified message.
	 * 
	 * @param msg
	 *            the message to send
	 * @throws MailException
	 *             if an error occurred sending the message
	 */
	public void send(final Message msg) throws MailException {
		Transport transport = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug("Sending mail message [subject={}, recipients={}]", msg.getSubject(),
						on(", ").join(msg.getAllRecipients()));
			}
			transport = sessionProvider.get().getTransport();
			transport.connect();
			transport.sendMessage(msg, msg.getAllRecipients());
		} catch (MessagingException ex) {
			throw new MailException("Error sending mail message", ex);
		} finally {
			try {
				transport.close();
			} catch (MessagingException ex) {
				log.error(ex.getMessage(), ex);
			}
		}
	}
}

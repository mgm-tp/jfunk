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

import java.util.Date;
import java.util.Enumeration;

import javax.mail.Address;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.mgmtp.jfunk.core.exception.MailException;

/**
 * @author rnaegele
 */
public class MailMessage {
	private final Date sentDate;
	private final Date receivedDate;
	private final String from;
	private final ListMultimap<RecipientType, String> recipients;
	private final String subject;
	private final String text;
	private final ListMultimap<String, String> headers;

	public static enum RecipientType {
		To, Cc, Bcc
	}

	private MailMessage(final Date sentDate, final Date receivedDate, final String from,
			final ListMultimap<RecipientType, String> recipients, final String subject,
			final String text, final ListMultimap<String, String> headers) {
		this.sentDate = sentDate;
		this.receivedDate = receivedDate;
		this.from = from;
		this.recipients = recipients;
		this.subject = subject;
		this.text = text;
		this.headers = headers;
	}

	public static MailMessage fromMessage(final Message message) {
		try {
			ListMultimap<RecipientType, String> recipients = createRecipients(message);
			ListMultimap<String, String> headers = createHeaders(message);
			String text = MessageUtils.messageAsText(message, false);
			String from = message.getFrom()[0].toString();
			return new MailMessage(message.getSentDate(), message.getReceivedDate(), from, recipients, message.getSubject(),
					text, headers);
		} catch (MessagingException ex) {
			throw new MailException("Error creating MailMessage.", ex);
		}
	}

	private static ListMultimap<String, String> createHeaders(final Message message) throws MessagingException {
		ListMultimap<String, String> headers = ArrayListMultimap.create();

		for (@SuppressWarnings("unchecked")
		Enumeration<Header> headersEnum = message.getAllHeaders(); headersEnum.hasMoreElements();) {
			Header header = headersEnum.nextElement();
			headers.put(header.getName(), header.getValue());
		}

		return headers;
	}

	private static ListMultimap<RecipientType, String> createRecipients(final Message message) throws MessagingException {
		ListMultimap<RecipientType, String> recipients = ArrayListMultimap.create();

		for (javax.mail.Message.RecipientType recipientType : asList(javax.mail.Message.RecipientType.TO,
				javax.mail.Message.RecipientType.CC, javax.mail.Message.RecipientType.BCC)) {
			Address[] addresses = message.getRecipients(recipientType);
			if (addresses != null) {
				for (Address address : addresses) {
					recipients.put(RecipientType.valueOf(recipientType.toString()), address.toString());
				}
			}
		}

		return recipients;
	}

	/**
	 * @return the sentDate
	 */
	public Date getSentDate() {
		return sentDate;
	}

	/**
	 * @return the receivedDate
	 */
	public Date getReceivedDate() {
		return receivedDate;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @return the recipients
	 */
	public ListMultimap<RecipientType, String> getRecipients() {
		return recipients;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return the headers
	 */
	public ListMultimap<String, String> getHeaders() {
		return headers;
	}
}

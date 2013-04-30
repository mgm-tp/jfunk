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

import java.io.IOException;
import java.util.Enumeration;

import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;

import org.apache.commons.lang3.text.StrBuilder;

import com.mgmtp.jfunk.core.exception.MailException;

/**
 * Utility class for {@link Message}s.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
public class MessageUtils {

	/**
	 * Returns the specified message as text, including headers.
	 * 
	 * @param message
	 *            the message
	 * @return the message text
	 */
	public static String messageAsText(final Message message) {
		return messageAsText(message, true);
	}

	/**
	 * Returns the specified message as text.
	 * 
	 * @param message
	 *            the message
	 * @param includeHeaders
	 *            specifies whether message headers are to be included in the returned text
	 * @return the message text
	 */
	public static String messageAsText(final Message message, final boolean includeHeaders) {
		try {
			StrBuilder sb = new StrBuilder(300);

			if (includeHeaders) {
				@SuppressWarnings("unchecked")
				Enumeration<Header> headers = message.getAllHeaders();
				while (headers.hasMoreElements()) {
					Header header = headers.nextElement();
					sb.append(header.getName()).append('=').appendln(header.getValue());
				}

				sb.appendln("");
			}

			Object content = message.getContent();
			if (content instanceof String) {
				String body = (String) content;
				sb.appendln(body);
				sb.appendln("");
			} else if (content instanceof Multipart) {
				parseMultipart(sb, (Multipart) content);
			}
			return sb.toString();
		} catch (MessagingException ex) {
			throw new MailException("Error getting mail content.", ex);
		} catch (IOException ex) {
			throw new MailException("Error getting mail content.", ex);
		}
	}

	private static void parseMultipart(final StrBuilder sb, final Multipart multipart) throws MessagingException, IOException {
		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			String disposition = bodyPart.getDisposition();

			if (disposition == null && bodyPart instanceof MimeBodyPart) { // not an attachment
				MimeBodyPart mimeBodyPart = (MimeBodyPart) bodyPart;

				if (mimeBodyPart.getContent() instanceof Multipart) {
					parseMultipart(sb, (Multipart) mimeBodyPart.getContent());
				} else {
					String body = (String) mimeBodyPart.getContent();
					sb.appendln(body);
					sb.appendln("");
				}
			}
		}
	}
}

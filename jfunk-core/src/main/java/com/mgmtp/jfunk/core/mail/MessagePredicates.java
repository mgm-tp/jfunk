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

import java.util.regex.Pattern;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Predicate;
import com.mgmtp.jfunk.core.exception.MailException;

/**
 * Predicates for mail messages.
 * 
 * @author rnaegele
 * @author jdost
 * @since 3.1.0
 */
public class MessagePredicates {

	/**
	 * Creates a {@link Predicate} for matching a mail header. If multiple header values are present
	 * for the given header name this Predicate returns true if at least one header value matches
	 * the given header value.
	 * 
	 * @param header
	 *            the header to match
	 * @return the predicate
	 */
	public static Predicate<Message> forHeader(final Header header) {
		return new Predicate<Message>() {
			@Override
			public boolean apply(final Message input) {
				try {
					String[] headers = input.getHeader(header.getName());
					if (headers.length == 0) {
						return false;
					}
					for (String singleHeader : headers) {
						if (StringUtils.equals(singleHeader, header.getValue())) {
							return true;
						}
					}
					return false;
				} catch (MessagingException ex) {
					throw new MailException(ex.getMessage(), ex);
				}
			}

			@Override
			public String toString() {
				return String.format("headers to include header '%s'", header.toString());
			}
		};
	}

	/**
	 * Creates a {@link Predicate} for matching a mail header and subject. If multiple header values
	 * are present for the given header name this Predicate returns true if at least one header
	 * value matches the given header value.
	 * 
	 * @param header
	 *            the header to match
	 * @param subjectPattern
	 *            the regex pattern to match
	 * @return the predicate
	 */
	public static Predicate<Message> forHeaderAndSubject(final Header header, final Pattern subjectPattern) {
		return new Predicate<Message>() {
			@Override
			public boolean apply(final Message input) {
				try {
					boolean result = false;
					String[] headers = input.getHeader(header.getName());
					if (headers.length != 0) {
						for (String singleHeader : headers) {
							if (StringUtils.equals(singleHeader, header.getValue())) {
								result = true;
								break;
							}
						}
					}
					return result && subjectPattern.matcher(input.getSubject()).matches();
				} catch (MessagingException ex) {
					throw new MailException(ex.getMessage(), ex);
				}
			}

			@Override
			public String toString() {
				return String.format("headers to include header '%s=%s' and subject to match pattern '%s'",
						header.getName(), header.getValue(), subjectPattern);
			}
		};
	}

	/**
	 * Creates a {@link Predicate} for matching the subject only.
	 * 
	 * @param subjectPattern
	 *            the regex pattern to match
	 * @return the predicate
	 */
	public static Predicate<Message> forSubject(final String subjectPattern) {
		return forSubject(Pattern.compile(subjectPattern));
	}

	/**
	 * Creates a {@link Predicate} for matching the subject only.
	 * 
	 * @param subjectPattern
	 *            the regex pattern to match
	 * @return the predicate
	 */
	public static Predicate<Message> forSubject(final Pattern subjectPattern) {
		return new Predicate<Message>() {
			@Override
			public boolean apply(final Message input) {
				try {
					return subjectPattern.matcher(input.getSubject()).matches();
				} catch (MessagingException ex) {
					throw new MailException(ex.getMessage(), ex);
				}
			}

			@Override
			public String toString() {
				return String.format("subject to match pattern '%s'", subjectPattern);
			}
		};
	}

	/**
	 * Creates a {@link Predicate} for matching subject and body.
	 * 
	 * @param subjectPattern
	 *            the regex pattern the subject must match
	 * @param bodyPattern
	 *            the regex pattern the body must match
	 * @return the predicate
	 */
	public static Predicate<Message> forSubjectAndBody(final String subjectPattern, final String bodyPattern) {
		return forSubjectAndBody(Pattern.compile(subjectPattern), Pattern.compile(bodyPattern));
	}

	/**
	 * Creates a {@link Predicate} for matching subject and body.
	 * 
	 * @param subjectPattern
	 *            the regex pattern the subject must match
	 * @param bodyPattern
	 *            the regex pattern the body must match
	 * @return the predicate
	 */
	public static Predicate<Message> forSubjectAndBody(final Pattern subjectPattern, final Pattern bodyPattern) {
		return new Predicate<Message>() {
			@Override
			public boolean apply(final Message input) {
				try {
					boolean result = subjectPattern.matcher(input.getSubject()).matches();
					return result && bodyPattern.matcher(MessageUtils.messageAsText(input, false)).matches();
				} catch (MessagingException ex) {
					throw new MailException(ex.getMessage(), ex);
				}
			}

			@Override
			public String toString() {
				return String.format("subject to match pattern '%s' and body to match pattern '%s'",
						subjectPattern, bodyPattern);
			}
		};
	}
}

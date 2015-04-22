/*
 * Copyright (c) 2015 mgm technology partners GmbH
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

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Predicate;

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
	 * @param headerName
	 *            the header name to match
	 * @param headerValue
	 *            the header value to match
	 * @return the predicate
	 */
	public static Predicate<MailMessage> forHeader(final String headerName, final String headerValue) {
		return new Predicate<MailMessage>() {
			@Override
			public boolean apply(final MailMessage input) {
				List<String> headers = input.getHeaders().get(headerName);
				for (String singleHeader : headers) {
					if (StringUtils.equals(singleHeader, headerValue)) {
						return true;
					}
				}
				return false;
			}

			@Override
			public String toString() {
				return String.format("headers to include header '%s=%s'", headerName, headerValue);
			}
		};
	}

	/**
	 * Creates a {@link Predicate} for matching a mail header and subject. If multiple header values
	 * are present for the given header name this Predicate returns true if at least one header
	 * value matches the given header value.
	 * 
	 * @param subjectPattern
	 *            the regex pattern to match
	 * @param headerName
	 *            the header name to match
	 * @param headerValue
	 *            the header value to match
	 * @return the predicate
	 */
	public static Predicate<MailMessage> forSubjectAndHeaders(final Pattern subjectPattern, final String headerName,
			final String headerValue) {
		return new Predicate<MailMessage>() {
			@Override
			public boolean apply(final MailMessage input) {
				boolean result = false;
				List<String> headers = input.getHeaders().get(headerName);
				for (String singleHeader : headers) {
					if (StringUtils.equals(singleHeader, headerValue)) {
						result = true;
						break;
					}
				}
				return result && subjectPattern.matcher(input.getSubject()).matches();
			}

			@Override
			public String toString() {
				return String.format("subject to match pattern '%s' and headers to include header '%s=%s'",
						subjectPattern, headerName, headerValue);
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
	public static Predicate<MailMessage> forSubject(final String subjectPattern) {
		return forSubject(Pattern.compile(subjectPattern));
	}

	/**
	 * Creates a {@link Predicate} for matching the subject only.
	 * 
	 * @param subjectPattern
	 *            the regex pattern to match
	 * @return the predicate
	 */
	public static Predicate<MailMessage> forSubject(final Pattern subjectPattern) {
		return new Predicate<MailMessage>() {
			@Override
			public boolean apply(final MailMessage input) {
				return subjectPattern.matcher(input.getSubject()).matches();
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
	public static Predicate<MailMessage> forSubjectAndBody(final String subjectPattern, final String bodyPattern) {
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
	public static Predicate<MailMessage> forSubjectAndBody(final Pattern subjectPattern, final Pattern bodyPattern) {
		return new Predicate<MailMessage>() {
			@Override
			public boolean apply(final MailMessage input) {
				boolean result = subjectPattern.matcher(input.getSubject()).matches();
				return result && bodyPattern.matcher(input.getText()).matches();
			}

			@Override
			public String toString() {
				return String.format("subject to match pattern '%s' and body to match pattern '%s'",
						subjectPattern, bodyPattern);
			}
		};
	}
}

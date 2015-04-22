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
package com.mgmtp.jfunk.common.util;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility functions.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
public class JFunkUtils {

	private static final Pattern NBSP_PATTERN = Pattern.compile("\\u00A0+");

	/**
	 * Removes leading and trailing whitespace and replaces sequences of whitespace characters by a single space. As opposed to
	 * {@link Character#isWhitespace(char)} or {@link StringUtils#isWhitespace(CharSequence)}, this funtion also considers
	 * non-breaking spaces (ASCII 160) as whitespace.
	 * 
	 * @param value
	 *            the string to normalize whitespaces from, may be null
	 * @return the modified string with whitespace normalized, or {@code null} if null was passed in
	 */
	public static String normalizeSpace(final String value) {
		if (value == null) {
			return null;
		}
		String result = NBSP_PATTERN.matcher(value).replaceAll(" ");
		return StringUtils.normalizeSpace(result);
	}
}

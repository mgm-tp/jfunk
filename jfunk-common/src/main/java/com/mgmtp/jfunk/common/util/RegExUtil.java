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
package com.mgmtp.jfunk.common.util;

import java.util.regex.Matcher;

/**
 * This class contains helper methods for reg ex handling.
 * 
 */
public final class RegExUtil {

	private static final String HYPHEN_MINUS = "\u002d";
	private static final String SOFT_HYPHEN = "\u00ad";

	private RegExUtil() {
		// don't allow instantiation
	}

	/**
	 * To be able to use generated data within a regular expression we need to escape characters
	 * that are otherwise interpreted as special characters. Currently the following characters are
	 * escaped:
	 * <ul>
	 * <li><code>\</code></li>
	 * <li><code>{</code></li>
	 * <li><code>}</code></li>
	 * <li><code>(</code></li>
	 * <li><code>)</code></li>
	 * <li><code>[</code></li>
	 * <li><code>]</code></li>
	 * <li><code>*</code></li>
	 * <li><code>+</code></li>
	 * <li><code>$</code></li>
	 * <li><code>^</code></li>
	 * <li><code>?</code></li>
	 * <li><code>.</code></li>
	 * </ul>
	 * 
	 * @param input
	 *            the string which we want to use in a regular expression
	 * @return the input string with all special characters escaped
	 */
	public static String escape(final String input) {
		String output;

		output = input.replaceAll("\\\\", Matcher.quoteReplacement("\\\\"));
		output = output.replaceAll("\\{", Matcher.quoteReplacement("\\{"));
		output = output.replaceAll("\\}", Matcher.quoteReplacement("\\}"));
		output = output.replaceAll("\\(", Matcher.quoteReplacement("\\("));
		output = output.replaceAll("\\)", Matcher.quoteReplacement("\\)"));
		output = output.replaceAll("\\[", Matcher.quoteReplacement("\\["));
		output = output.replaceAll("\\]", Matcher.quoteReplacement("\\]"));
		output = output.replaceAll("\\*", Matcher.quoteReplacement("\\*"));
		output = output.replaceAll("\\+", Matcher.quoteReplacement("\\+"));
		output = output.replaceAll("\\$", Matcher.quoteReplacement("\\$"));
		output = output.replaceAll("\\^", Matcher.quoteReplacement("\\^"));
		output = output.replaceAll("\\?", Matcher.quoteReplacement("\\?"));
		output = output.replaceAll("\\.", Matcher.quoteReplacement("\\."));
		output = output.replaceAll("\\|", Matcher.quoteReplacement("\\|"));

		return output;
	}

	/**
	 * Replaces a space with the regular expression \\s?
	 * 
	 * @param input
	 *            the string to modify
	 * @return the input string with all spaces replaced by \\s?
	 */
	public static String spacesOptional(final String input) {
		String output;
		output = input.replaceAll("\\s", "\\\\s?");
		return output;
	}

	/**
	 * Replaces both 'hyphen minus' (Unicode U+002D) and 'soft hyphen' (Unicode U+00AD) characters
	 * with the character itself followed by a '?'.
	 * 
	 * @param input
	 *            the string to modify
	 * @return the modified input string with all replacements described above
	 */
	public static String hyphenOptional(final String input) {
		return input.replaceAll(HYPHEN_MINUS, HYPHEN_MINUS + "?").replaceAll(SOFT_HYPHEN, SOFT_HYPHEN + "?");
	}
}
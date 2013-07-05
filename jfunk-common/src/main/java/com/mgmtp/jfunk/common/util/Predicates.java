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

import java.util.regex.Pattern;

import com.google.common.base.Predicate;

/**
 * A {@link Predicate} factory class.
 * 
 */
public class Predicates {

	private Predicates() {
		// don't allow instantiation
	}

	/**
	 * Creates a {@link Predicate} that returns {@code true} for any string that matches the
	 * specified regular expression.
	 * 
	 * @param pattern
	 *            The pattern
	 * @return {@code true} for any string that matches its given regular expression
	 */
	public static Predicate<String> matchesRegex(final Pattern pattern) {
		return new Predicate<String>() {
			@Override
			public boolean apply(final String input) {
				return pattern.matcher(input).matches();
			}
		};
	}

	/**
	 * Creates a {@link Predicate} that returns {@code true} for any string that matches the
	 * specified regular expression.
	 * 
	 * @param regex
	 *            The pattern
	 * @return {@code true} for any string that matches its given regular expression
	 */
	public static Predicate<String> matchesRegex(final String regex) {
		final Pattern pattern = Pattern.compile(regex);
		return matchesRegex(pattern);
	}

	/**
	 * Creates a {@link Predicate} that returns {@code true} for any string that starts with the
	 * specified prefix.
	 * 
	 * @param prefix
	 *            The prefix
	 * @return {@code true} for any string that starts with the specified prefix
	 */
	public static Predicate<String> startsWith(final String prefix) {
		return new Predicate<String>() {
			@Override
			public boolean apply(final String input) {
				return input.startsWith(prefix);
			}
		};
	}

	/**
	 * Creates a {@link Predicate} that returns {@code true} for any string that ends with the
	 * specified suffix.
	 * 
	 * @param suffix
	 *            The suffix
	 * @return {@code true} for any string that ends with the specified suffix
	 */
	public static Predicate<String> endsWith(final String suffix) {
		return new Predicate<String>() {
			@Override
			public boolean apply(final String input) {
				return input.endsWith(suffix);
			}
		};
	}

	/**
	 * Creates a {@link Predicate} that returns {@code true} for any string that contains the
	 * specified prefix.
	 * 
	 * @param value
	 *            The value
	 * @return {@code true} for any string that contains the specified prefix
	 */
	public static Predicate<String> contains(final String value) {
		return new Predicate<String>() {
			@Override
			public boolean apply(final String input) {
				return input.contains(value);
			}
		};
	}
}
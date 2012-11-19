package com.mgmtp.jfunk.common.util;

import java.util.regex.Pattern;

import com.google.common.base.Predicate;

/**
 * A {@link Predicate} factory class.
 * 
 * @version $Id$
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
	 * specified prefix.
	 * 
	 * @param prefix
	 *            The prefix
	 * @return {@code true} for any string that ends with the specified prefix
	 */
	public static Predicate<String> endsWith(final String prefix) {
		return new Predicate<String>() {
			@Override
			public boolean apply(final String input) {
				return input.endsWith(prefix);
			}
		};
	}
}
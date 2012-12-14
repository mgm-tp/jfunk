/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.util;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import com.google.common.collect.ImmutableList;

/**
 * @author rnaegele
 * @version $Id: Predicates.java 28330 2012-10-09 13:16:34Z reinhard.naegele $
 */
public class WebDriverPredicates {

	private WebDriverPredicates() {
		// don't allow instantiation
	}

	public static BasePredicate<WebDriver, Void> urlMatchesPattern(final Pattern pattern) {
		return new UrlMatchesPatternPredicate(pattern);
	}

	public static BasePredicate<WebDriver, Void> urlMatchesPattern(final String regex) {
		return new UrlMatchesPatternPredicate(regex);
	}

	public static BasePredicate<WebDriver, Void> pageSourceContainsString(final String searchString) {
		return new PageSourceContainsStringPredicate(searchString, false, true);
	}

	public static BasePredicate<WebDriver, Void> pageSourceContainsString(final String searchString, final boolean caseSensitive) {
		return new PageSourceContainsStringPredicate(searchString, caseSensitive, true);
	}

	public static BasePredicate<WebDriver, Void> pageSourceContainsString(final String searchString, final boolean caseSensitive,
			final boolean mustExist) {
		return new PageSourceContainsStringPredicate(searchString, caseSensitive, mustExist);
	}

	public static BasePredicate<WebDriver, List<String>> pageSourceMatchesPattern(final String pattern, final boolean mustExist) {
		return new PageSourceMatchesPatternPredicate(pattern, mustExist);
	}

	public static BasePredicate<WebDriver, List<String>> pageSourceMatchesPattern(final Pattern pattern, final boolean mustExist) {
		return new PageSourceMatchesPatternPredicate(pattern, mustExist);
	}

	private static class UrlMatchesPatternPredicate extends BasePredicate<WebDriver, Void> {

		private final Pattern pattern;

		public UrlMatchesPatternPredicate(final Pattern pattern) {
			this.pattern = pattern;
		}

		public UrlMatchesPatternPredicate(final String regex) {
			this.pattern = Pattern.compile(regex);
		}

		@Override
		public boolean doApply(final WebDriver input) {
			return pattern.matcher(input.getCurrentUrl()).matches();
		}
	}

	private static class PageSourceContainsStringPredicate extends BasePredicate<WebDriver, Void> {

		private final String searchString;
		private final boolean caseSensitive;
		private final boolean mustExist;

		public PageSourceContainsStringPredicate(final String searchString, final boolean caseSensitive, final boolean mustExist) {
			this.searchString = searchString;
			this.caseSensitive = caseSensitive;
			this.mustExist = mustExist;
		}

		@Override
		public boolean doApply(final WebDriver input) {
			String pageSource = input.getPageSource().replaceAll("\\s+", " ");
			log.trace("pageSource={}", pageSource);

			boolean outcome = caseSensitive ? pageSource.contains(searchString) : containsIgnoreCase(pageSource, searchString);
			return mustExist ? outcome : !outcome;
		}
	}

	private static class PageSourceMatchesPatternPredicate extends BasePredicate<WebDriver, List<String>> {

		private final Pattern pattern;
		private final boolean mustExist;
		private List<String> groupValues;

		public PageSourceMatchesPatternPredicate(final String pattern, final boolean mustExist) {
			this(Pattern.compile(pattern), mustExist);
		}

		public PageSourceMatchesPatternPredicate(final Pattern pattern, final boolean mustExist) {
			this.pattern = pattern;
			this.mustExist = mustExist;
		}

		@Override
		public boolean doApply(final WebDriver input) {
			String pageSource = input.getPageSource().replaceAll("\\s+", " ");
			log.trace("pageSource={}", pageSource);

			Matcher matcher = pattern.matcher(pageSource);
			boolean outcome = mustExist && matcher.matches();

			if (mustExist && outcome) {
				int groupCount = matcher.groupCount();
				if (groupCount > 0) {
					groupValues = newArrayListWithCapacity(groupCount);

					for (int i = 1; i <= groupCount; ++i) {
						String groupValue = matcher.group(i);
						groupValues.add(groupValue);
					}
				} else {
					groupValues = ImmutableList.of();
				}
			}

			return outcome;
		}

		@Override
		public List<String> getResult() {
			return groupValues;
		}
	}
}

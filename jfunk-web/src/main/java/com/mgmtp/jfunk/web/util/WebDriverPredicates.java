/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.util;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;

import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.google.common.base.Predicate;

/**
 * @author rnaegele
 */
public class WebDriverPredicates {

	private WebDriverPredicates() {
		// don't allow instantiation
	}

	public static Predicate<WebDriver> urlMatchesPattern(final Pattern pattern) {
		return new UrlMatchesPatternPredicate(pattern);
	}

	public static Predicate<WebDriver> urlMatchesPattern(final String regex) {
		return new UrlMatchesPatternPredicate(regex);
	}

	public static Predicate<WebDriver> pageSourceContainsString(final String searchString) {
		return new PageSourceContainsStringPredicate(searchString, false, true);
	}

	public static Predicate<WebDriver> pageSourceContainsString(final String searchString, final boolean caseSensitive) {
		return new PageSourceContainsStringPredicate(searchString, caseSensitive, true);
	}

	public static Predicate<WebDriver> pageSourceContainsString(final String searchString, final boolean caseSensitive,
			final boolean mustExist) {
		return new PageSourceContainsStringPredicate(searchString, caseSensitive, mustExist);
	}

	public static Predicate<WebDriver> textEquals(final By locator, final String value) {
		return new TextEqualsPredicate(locator, value, true);
	}

	public static Predicate<WebDriver> textEquals(final By locator, final String value, final boolean normalizeSpace) {
		return new TextEqualsPredicate(locator, value, normalizeSpace);
	}

	public static Predicate<WebDriver> textContains(final By locator, final String value) {
		return new TextContainsPredicate(locator, value, true);
	}

	public static Predicate<WebDriver> textContains(final By locator, final String value, final boolean normalizeSpace) {
		return new TextContainsPredicate(locator, value, normalizeSpace);
	}

	public static Predicate<WebDriver> textMatchesPattern(final By locator, final Pattern pattern) {
		return new TextMatchesPatternPredicate(locator, pattern);
	}

	public static Predicate<WebDriver> textMatchesPattern(final By locator, final String regex) {
		return new TextMatchesPatternPredicate(locator, regex);
	}

	public static Predicate<WebDriver> attributeValueMatchesPattern(final By locator, final Pattern pattern,
			final String attribute) {
		return new AttributeValueMatchesPatternPredicate(locator, pattern, attribute);
	}

	public static Predicate<WebDriver> attributeValueMatchesPattern(final By locator, final String regex, final String attribute) {
		return new AttributeValueMatchesPatternPredicate(locator, regex, attribute);
	}

	private static class TextEqualsPredicate extends LocatorPredicate {
		private final String text;
		private final boolean normalizeSpace;
		private String currentText;

		public TextEqualsPredicate(final By locator, final String text, final boolean normalizeSpace) {
			super(locator);
			this.text = text;
			this.normalizeSpace = normalizeSpace;
		}

		@Override
		public boolean apply(final WebDriver input) {
			currentText = input.findElement(locator).getText();
			if (normalizeSpace) {
				currentText = normalizeSpace(currentText);
			}
			return currentText.equals(text);
		}

		@Override
		public String toString() {
			return String.format("%stext of element located by '%s' to be equal to '%s'. Current text: '%s'",
					normalizeSpace ? "normalized " : "", locator, text, currentText);
		}
	}

	private static class TextContainsPredicate extends LocatorPredicate {
		private final String text;
		private final boolean normalizeSpace;
		private String currentText;

		public TextContainsPredicate(final By locator, final String text, final boolean normalizeSpace) {
			super(locator);
			this.text = text;
			this.normalizeSpace = normalizeSpace;
		}

		@Override
		public boolean apply(final WebDriver input) {
			currentText = input.findElement(locator).getText();
			if (normalizeSpace) {
				currentText = normalizeSpace(text);
			}
			return currentText.contains(text);
		}

		@Override
		public String toString() {
			return String.format("%stext of element located by '%s' to contain '%s'. Current text: '%s'",
					normalizeSpace ? "normalized " : "", locator, text, currentText);
		}
	}

	private static class TextMatchesPatternPredicate extends LocatorPredicate {
		private final Pattern pattern;
		private String currentText;

		public TextMatchesPatternPredicate(final By locator, final Pattern pattern) {
			super(locator);
			this.pattern = pattern;
		}

		public TextMatchesPatternPredicate(final By locator, final String regex) {
			this(locator, Pattern.compile(regex));
		}

		@Override
		public boolean apply(final WebDriver input) {
			currentText = input.findElement(locator).getText();
			return pattern.matcher(currentText).matches();
		}

		@Override
		public String toString() {
			return String.format("text of element located by '%s' to match pattern '%s'. Current text: '%s'",
					pattern, locator, currentText);
		}
	}

	private static class AttributeValueMatchesPatternPredicate extends LocatorPredicate {
		private final Pattern pattern;
		private final String attribute;
		private String currentAttributeValue;

		public AttributeValueMatchesPatternPredicate(final By locator, final Pattern pattern, final String attribute) {
			super(locator);
			this.pattern = pattern;
			this.attribute = attribute;
		}

		public AttributeValueMatchesPatternPredicate(final By locator, final String regex, final String attribute) {
			this(locator, Pattern.compile(regex), attribute);
		}

		@Override
		public boolean apply(final WebDriver input) {
			currentAttributeValue = input.findElement(locator).getAttribute(attribute);
			return pattern.matcher(currentAttributeValue).matches();
		}

		@Override
		public String toString() {
			return String.format("attribute '%s' of element located by '%s' to match pattern '%s'. Current value: '%s'",
					attribute, locator, pattern, currentAttributeValue);
		}
	}

	private static class UrlMatchesPatternPredicate implements Predicate<WebDriver> {

		private final Pattern pattern;
		private String currentUrl;

		public UrlMatchesPatternPredicate(final Pattern pattern) {
			this.pattern = pattern;
		}

		public UrlMatchesPatternPredicate(final String regex) {
			this.pattern = Pattern.compile(regex);
		}

		@Override
		public boolean apply(final WebDriver input) {
			currentUrl = input.getCurrentUrl();
			return pattern.matcher(currentUrl).matches();
		}

		@Override
		public String toString() {
			return String.format("current url (%s) to match pattern '%s'", currentUrl, pattern);
		}
	}

	private static class PageSourceContainsStringPredicate implements Predicate<WebDriver> {

		private final String searchString;
		private final boolean caseSensitive;
		private final boolean mustExist;

		public PageSourceContainsStringPredicate(final String searchString, final boolean caseSensitive, final boolean mustExist) {
			this.searchString = searchString;
			this.caseSensitive = caseSensitive;
			this.mustExist = mustExist;
		}

		@Override
		public boolean apply(final WebDriver input) {
			String pageSource = input.getPageSource().replaceAll("\\s+", " ");

			boolean outcome = caseSensitive ? pageSource.contains(searchString) : containsIgnoreCase(pageSource, searchString);
			return mustExist ? outcome : !outcome;
		}

		@Override
		public String toString() {
			return String.format("page source %sto contain '%s' (%scase-sensitive)",
					mustExist ? "" : "not ", searchString, caseSensitive ? "" : "not ");
		}
	}
}

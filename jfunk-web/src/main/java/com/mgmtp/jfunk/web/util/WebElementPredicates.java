/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.util;

import static org.apache.commons.lang3.StringUtils.normalizeSpace;

import java.util.regex.Pattern;

import org.openqa.selenium.WebElement;

import com.google.common.base.Predicate;

/**
 * @author rnaegele
 * @version $Id: Predicates.java 28330 2012-10-09 13:16:34Z reinhard.naegele $
 */
public class WebElementPredicates {

	private WebElementPredicates() {
		// don't allow instantiation
	}

	public static Predicate<WebElement> textEquals(final String value) {
		return new TextEqualsPredicate(value, true);
	}

	public static Predicate<WebElement> textEquals(final String value, final boolean normalizeSpace) {
		return new TextEqualsPredicate(value, normalizeSpace);
	}

	public static Predicate<WebElement> textContains(final String value) {
		return new TextContainsPredicate(value, true);
	}

	public static Predicate<WebElement> textContains(final String value, final boolean normalizeSpace) {
		return new TextContainsPredicate(value, normalizeSpace);
	}

	public static Predicate<WebElement> textMatchesPattern(final Pattern pattern) {
		return new TextMatchesPatternPredicate(pattern);
	}

	public static Predicate<WebElement> textMatchesPattern(final String regex) {
		return new TextMatchesPatternPredicate(regex);
	}

	public static Predicate<WebElement> attributeValueMatchesPattern(final Pattern pattern, final String attribute) {
		return new AttributeValueMatchesPatternPredicate(pattern, attribute);
	}

	public static Predicate<WebElement> attributeValueMatchesPattern(final String regex, final String attribute) {
		return new AttributeValueMatchesPatternPredicate(regex, attribute);
	}

	private static class TextEqualsPredicate implements Predicate<WebElement> {

		private final String text;
		private final boolean normalizeSpace;
		private String currentText;

		public TextEqualsPredicate(final String text, final boolean normalizeSpace) {
			this.text = text;
			this.normalizeSpace = normalizeSpace;
		}

		@Override
		public boolean apply(final WebElement input) {
			currentText = input.getText();
			if (normalizeSpace) {
				currentText = normalizeSpace(currentText);
			}
			return currentText.equals(text);
		}

		@Override
		public String toString() {
			return String.format("%selement text to be '%s'. Current text: '%s'",
					normalizeSpace ? "normalized " : "", text, currentText);
		}
	}

	private static class TextContainsPredicate implements Predicate<WebElement> {

		private final String text;
		private final boolean normalizeSpace;
		private String currentText;

		public TextContainsPredicate(final String text, final boolean normalizeSpace) {
			this.text = text;
			this.normalizeSpace = normalizeSpace;
		}

		@Override
		public boolean apply(final WebElement input) {
			currentText = input.getText();
			if (normalizeSpace) {
				currentText = normalizeSpace(text);
			}
			return currentText.contains(text);
		}

		@Override
		public String toString() {
			return String.format("%selement text to contain '%s'. Current text: '%s'",
					normalizeSpace ? "normalized " : "", text, currentText);
		}
	}

	private static class TextMatchesPatternPredicate implements Predicate<WebElement> {

		private final Pattern pattern;
		private String currentText;

		public TextMatchesPatternPredicate(final Pattern pattern) {
			this.pattern = pattern;
		}

		public TextMatchesPatternPredicate(final String regex) {
			this(Pattern.compile(regex));
		}

		@Override
		public boolean apply(final WebElement input) {
			currentText = input.getText();
			return pattern.matcher(currentText).matches();
		}

		@Override
		public String toString() {
			return String.format("element text to match pattern '%s'. Current text: '%s'", pattern, currentText);
		}
	}

	private static class AttributeValueMatchesPatternPredicate implements Predicate<WebElement> {

		private final Pattern pattern;
		private final String attribute;
		private String currentAttributeValue;

		public AttributeValueMatchesPatternPredicate(final Pattern pattern, final String attribute) {
			this.pattern = pattern;
			this.attribute = attribute;
		}

		public AttributeValueMatchesPatternPredicate(final String regex, final String attribute) {
			this(Pattern.compile(regex), attribute);
		}

		@Override
		public boolean apply(final WebElement input) {
			currentAttributeValue = input.getAttribute(attribute);
			return pattern.matcher(currentAttributeValue).matches();
		}

		@Override
		public String toString() {
			return String.format("attribute '%s' to match pattern '%s'. Current value: '%s'",
					attribute, pattern, currentAttributeValue);
		}
	}
}

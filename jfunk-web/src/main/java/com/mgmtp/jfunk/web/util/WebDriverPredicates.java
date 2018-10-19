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
package com.mgmtp.jfunk.web.util;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;

import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;

/**
 * {@link Predicate}s that may be used similarly to {@link ExpectedConditions}.
 * 
 * @author rnaegele
 * @author eneumann
 * @deprecated as of 3.3.1, use {@link WebDriverFunctions} instead
 */
@Deprecated
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
		return new PageSourceContainsStringPredicate(searchString, false);
	}

	public static Predicate<WebDriver> pageSourceContainsString(final String searchString, final boolean caseSensitive) {
		return new PageSourceContainsStringPredicate(searchString, caseSensitive);
	}

	public static Predicate<WebDriver> pageSourceMatchesPattern(final Pattern pattern) {
		return new PageSourceMatchesPatternFunction(pattern);
	}

	public static Predicate<WebDriver> pageSourceMatchesPattern(final String pattern) {
		return new PageSourceMatchesPatternFunction(Pattern.compile(pattern));
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

	public static Predicate<WebDriver> pageToBeLoaded() {
		return new PageToBeLoadedPredicate();
	}

	public static Predicate<WebDriver> refreshOnFalseNullOrException(final Predicate<WebDriver> delegate) {
		return new RefreshOnFalseNullOrExceptionWrapperPredicate(delegate);
	}

	public static Predicate<WebDriver> alertIsPresent() {
		return new AlertIsPresentPredicate();
	}

	public static Predicate<WebDriver> alertTextEquals(final String text) {
		return new AlertTextEqualsPredicate(text);
	}

	public static Predicate<WebDriver> alertTextMatchesPattern(final Pattern pattern) {
		return new AlertTextMatchesPatternPredicate(pattern);
	}

	public static Predicate<WebDriver> alertTextMatchesPattern(final String pattern) {
		return new AlertTextMatchesPatternPredicate(Pattern.compile(pattern));
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

	private static class PageToBeLoadedPredicate implements Predicate<WebDriver> {

		@Override
		public boolean apply(final WebDriver input) {
			return ((JavascriptExecutor) input).executeScript("return document.readyState").equals("complete");
		}

		@Override
		public String toString() {
			return "page to be loaded";
		}
	}

	private static class PageSourceContainsStringPredicate implements Predicate<WebDriver> {

		private final String searchString;
		private final boolean caseSensitive;

		public PageSourceContainsStringPredicate(final String searchString, final boolean caseSensitive) {
			this.searchString = searchString;
			this.caseSensitive = caseSensitive;
		}

		@Override
		public boolean apply(final WebDriver input) {
			String pageSource = normalizeSpace(input.getPageSource());
			return caseSensitive ? pageSource.contains(searchString) : containsIgnoreCase(pageSource, searchString);
		}

		@Override
		public String toString() {
			return String.format("page source to contain '%s' (%scase-sensitive)", searchString, caseSensitive ? "" : "not ");
		}
	}

	private static class PageSourceMatchesPatternFunction implements Predicate<WebDriver> {

		private final Pattern pattern;

		public PageSourceMatchesPatternFunction(final Pattern pattern) {
			this.pattern = pattern;
		}

		@Override
		public boolean apply(final WebDriver input) {
			String pageSource = normalizeSpace(input.getPageSource());
			return pattern.matcher(pageSource).matches();
		}

		@Override
		public String toString() {
			return String.format("page to match pattern '%s'", pattern);
		}
	}

	private static class RefreshOnFalseNullOrExceptionWrapperPredicate implements Predicate<WebDriver> {
		private final Function<WebDriver, Boolean> delegateFunction;
		private final Predicate<WebDriver> delegate;

		public RefreshOnFalseNullOrExceptionWrapperPredicate(final Predicate<WebDriver> delegate) {
			this.delegate = delegate;
			this.delegateFunction = WebDriverFunctions.refreshOnFalseNullOrException(Functions.forPredicate(delegate));
		}

		@Override
		public boolean apply(final WebDriver input) {
			return delegateFunction.apply(input);
		}

		@Override
		public String toString() {
			return delegate.toString();
		}
	}

	private static class AlertIsPresentPredicate implements Predicate<WebDriver> {
		@Override
		public boolean apply(final WebDriver input) {
			try {
				input.switchTo().alert();
				return true;
			} catch (NoAlertPresentException nape) {
				return false;
			}
		}

		@Override
		public String toString() {
			return "alert to be present";
		}
	}

	private static class AlertTextEqualsPredicate implements Predicate<WebDriver> {
		private final String text;
		private String alertText;

		public AlertTextEqualsPredicate(final String text) {
			this.text = text;
		}

		@Override
		public boolean apply(final WebDriver input) {
			try {
				alertText = input.switchTo().alert().getText();
				return alertText.equals(text);
			} catch (NoAlertPresentException nape) {
				return false;
			}
		}

		@Override
		public String toString() {
			return String.format("alert text to equal '%s'. Current text: '%s'", text, alertText);
		}
	}

	private static class AlertTextMatchesPatternPredicate implements Predicate<WebDriver> {
		private final Pattern pattern;
		private String alertText;

		public AlertTextMatchesPatternPredicate(final Pattern pattern) {
			this.pattern = pattern;
		}

		@Override
		public boolean apply(final WebDriver input) {
			try {
				alertText = input.switchTo().alert().getText();
				return pattern.matcher(alertText).matches();
			} catch (NoAlertPresentException nape) {
				return false;
			}
		}

		@Override
		public String toString() {
			return String.format("alert text to match pattern '%s'. Current text: '%s'", pattern, alertText);
		}
	}
}

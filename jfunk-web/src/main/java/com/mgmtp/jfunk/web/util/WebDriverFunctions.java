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

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 * {@link Function}s that may be used e. g. in {@link WebDriverTool#waitFor(Function)} similarly to
 * {@link ExpectedConditions}.
 * 
 * @author rnaegele
 */
public class WebDriverFunctions {

	private WebDriverFunctions() {
		// don't allow instantiation
	}
	
	public static Function<WebDriver, Boolean> urlMatchesPattern(final Pattern pattern) {
		return new UrlMatchesPatternFunction(pattern);
	}

	public static Function<WebDriver, Boolean> urlMatchesPattern(final String regex) {
		return new UrlMatchesPatternFunction(regex);
	}

	public static Function<WebDriver, Boolean> pageSourceContainsString(final String searchString) {
		return new PageSourceContainsStringFunction(searchString, false);
	}

	public static Function<WebDriver, Boolean> pageSourceContainsString(final String searchString, final boolean caseSensitive) {
		return new PageSourceContainsStringFunction(searchString, caseSensitive);
	}

	public static Function<WebDriver, List<String>> pageSourceMatchesPattern(final String pattern) {
		return new PageSourceMatchesPatternFunction(Pattern.compile(pattern));
	}

	public static Function<WebDriver, List<String>> pageSourceMatchesPattern(final Pattern pattern) {
		return new PageSourceMatchesPatternFunction(pattern);
	}

	public static Function<WebDriver, Boolean> textEquals(final By locator, final String value) {
		return new TextEqualsFunction(locator, value, true);
	}

	public static Function<WebDriver, Boolean> textEquals(final By locator, final String value, final boolean normalizeSpace) {
		return new TextEqualsFunction(locator, value, normalizeSpace);
	}

	public static Function<WebDriver, Boolean> textContains(final By locator, final String value) {
		return new TextContainsFunction(locator, value, true);
	}

	public static Function<WebDriver, Boolean> textContains(final By locator, final String value, final boolean normalizeSpace) {
		return new TextContainsFunction(locator, value, normalizeSpace);
	}

	public static Function<WebDriver, Boolean> textMatchesPattern(final By locator, final Pattern pattern) {
		return new TextMatchesPatternFunction(locator, pattern);
	}

	public static Function<WebDriver, Boolean> textMatchesPattern(final By locator, final String regex) {
		return new TextMatchesPatternFunction(locator, regex);
	}

	public static Function<WebDriver, Boolean> attributeValueMatchesPattern(final By locator, final Pattern pattern,
			final String attribute) {
		return new AttributeValueMatchesPatternFunction(locator, pattern, attribute);
	}

	public static Function<WebDriver, Boolean> attributeValueMatchesPattern(final By locator, final String regex, final String attribute) {
		return new AttributeValueMatchesPatternFunction(locator, regex, attribute);
	}

	public static Function<WebDriver, Boolean> pageToBeLoaded() {
		return new PageToBeLoadedFunction();
	}

	public static <V> Function<WebDriver, V> refreshOnFalseNullOrException(final Function<WebDriver, V> delegate) {
		return new RefreshOnFalseNullOrExceptionWrapperFunction<>(delegate);
	}

	public static Function<WebDriver, Boolean> alertIsPresent() {
		return new AlertIsPresentFunction();
	}

	public static Function<WebDriver, Boolean> alertTextEquals(final String text) {
		return new AlertTextEqualsFunction(text);
	}

	public static Function<WebDriver, Boolean> alertTextMatchesPattern(final Pattern pattern) {
		return new AlertTextMatchesPatternFunction(pattern);
	}

	public static Function<WebDriver, Boolean> alertTextMatchesPattern(final String pattern) {
		return new AlertTextMatchesPatternFunction(Pattern.compile(pattern));
	}

	private static class TextEqualsFunction extends LocatorFunction<Boolean> {
		private final String text;
		private final boolean normalizeSpace;
		private String currentText;

		public TextEqualsFunction(final By locator, final String text, final boolean normalizeSpace) {
			super(locator);
			this.text = text;
			this.normalizeSpace = normalizeSpace;
		}

		@Override
		public Boolean apply(final WebDriver input) {
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

	private static class TextContainsFunction extends LocatorFunction<Boolean> {
		private final String text;
		private final boolean normalizeSpace;
		private String currentText;

		public TextContainsFunction(final By locator, final String text, final boolean normalizeSpace) {
			super(locator);
			this.text = text;
			this.normalizeSpace = normalizeSpace;
		}

		@Override
		public Boolean apply(final WebDriver input) {
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

	private static class TextMatchesPatternFunction extends LocatorFunction<Boolean> {
		private final Pattern pattern;
		private String currentText;

		public TextMatchesPatternFunction(final By locator, final Pattern pattern) {
			super(locator);
			this.pattern = pattern;
		}

		public TextMatchesPatternFunction(final By locator, final String regex) {
			this(locator, Pattern.compile(regex));
		}

		@Override
		public Boolean apply(final WebDriver input) {
			currentText = input.findElement(locator).getText();
			return pattern.matcher(currentText).matches();
		}

		@Override
		public String toString() {
			return String.format("text of element located by '%s' to match pattern '%s'. Current text: '%s'",
					pattern, locator, currentText);
		}
	}

	private static class AttributeValueMatchesPatternFunction extends LocatorFunction<Boolean> {
		private final Pattern pattern;
		private final String attribute;
		private String currentAttributeValue;

		public AttributeValueMatchesPatternFunction(final By locator, final Pattern pattern, final String attribute) {
			super(locator);
			this.pattern = pattern;
			this.attribute = attribute;
		}

		public AttributeValueMatchesPatternFunction(final By locator, final String regex, final String attribute) {
			this(locator, Pattern.compile(regex), attribute);
		}

		@Override
		public Boolean apply(final WebDriver input) {
			currentAttributeValue = input.findElement(locator).getAttribute(attribute);
			return pattern.matcher(currentAttributeValue).matches();
		}

		@Override
		public String toString() {
			return String.format("attribute '%s' of element located by '%s' to match pattern '%s'. Current value: '%s'",
					attribute, locator, pattern, currentAttributeValue);
		}
	}

	private static class UrlMatchesPatternFunction implements Function<WebDriver, Boolean> {

		private final Pattern pattern;
		private String currentUrl;

		public UrlMatchesPatternFunction(final Pattern pattern) {
			this.pattern = pattern;
		}

		public UrlMatchesPatternFunction(final String regex) {
			this.pattern = Pattern.compile(regex);
		}

		@Override
		public Boolean apply(final WebDriver input) {
			currentUrl = input.getCurrentUrl();
			return pattern.matcher(currentUrl).matches();
		}

		@Override
		public String toString() {
			return String.format("current url (%s) to match pattern '%s'", currentUrl, pattern);
		}
	}

	private static class PageToBeLoadedFunction implements Function<WebDriver, Boolean> {

		@Override
		public Boolean apply(final WebDriver input) {
			return ((JavascriptExecutor) input).executeScript("return document.readyState").equals("complete");
		}

		@Override
		public String toString() {
			return "page to be loaded";
		}
	}

	private static class PageSourceContainsStringFunction implements Function<WebDriver, Boolean> {

		private final String searchString;
		private final boolean caseSensitive;

		public PageSourceContainsStringFunction(final String searchString, final boolean caseSensitive) {
			this.searchString = searchString;
			this.caseSensitive = caseSensitive;
		}

		@Override
		public Boolean apply(final WebDriver input) {
			String pageSource = normalizeSpace(input.getPageSource());
			return caseSensitive ? pageSource.contains(searchString) : containsIgnoreCase(pageSource, searchString);
		}

		@Override
		public String toString() {
			return String.format("page source to contain '%s' (%scase-sensitive)", searchString, caseSensitive ? "" : "not ");
		}
	}

	private static class PageSourceMatchesPatternFunction implements Function<WebDriver, List<String>> {

		private final Pattern pattern;

		public PageSourceMatchesPatternFunction(final Pattern pattern) {
			this.pattern = pattern;
		}

		@Override
		public List<String> apply(final WebDriver input) {
			String pageSource = normalizeSpace(input.getPageSource());

			Matcher matcher = pattern.matcher(pageSource);

			List<String> groupValues = null;
			if (matcher.matches()) {
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

				return groupValues;
			}

			// return null in case of failure!!!
			return null;
		}

		@Override
		public String toString() {
			return String.format("page to match pattern '%s'", pattern);
		}
	}

	private static class RefreshOnFalseNullOrExceptionWrapperFunction<V> implements Function<WebDriver, V> {
		private final Logger log = LoggerFactory.getLogger(getClass());

		private final Function<WebDriver, V> delegate;

		public RefreshOnFalseNullOrExceptionWrapperFunction(final Function<WebDriver, V> delegate) {
			this.delegate = delegate;
		}

		@Override
		public V apply(final WebDriver input) {
			boolean needsRefresh = true;
			try {
				V result = delegate.apply(input);
				needsRefresh = result instanceof Boolean && !(Boolean) result || result == null;
				return result;
			} finally {
				if (needsRefresh) {
					log.trace("Refreshing page...");
					input.navigate().refresh();
				}
			}
		}

		@Override
		public String toString() {
			return delegate.toString();
		}
	}

	private static class AlertIsPresentFunction implements Function<WebDriver, Boolean> {
		@Override
		public Boolean apply(final WebDriver input) {
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

	private static class AlertTextEqualsFunction implements Function<WebDriver, Boolean> {
		private final String text;
		private String alertText;

		public AlertTextEqualsFunction(final String text) {
			this.text = text;
		}

		@Override
		public Boolean apply(final WebDriver input) {
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

	private static class AlertTextMatchesPatternFunction implements Function<WebDriver, Boolean> {
		private final Pattern pattern;
		private String alertText;

		public AlertTextMatchesPatternFunction(final Pattern pattern) {
			this.pattern = pattern;
		}

		@Override
		public Boolean apply(final WebDriver input) {
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

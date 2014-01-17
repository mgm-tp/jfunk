/*
 * Copyright (c) 2014 mgm technology partners GmbH
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

/**
 * <p>
 * Provides a fluent interface for finding elements, optionally with timeout and constraints (enabled, displayed, selected).
 * </p>
 * <p>
 * <strong>Warning: {@link WebElementFinder} instances are always immutable</strong>.; Configuration methods have no effect on the
 * instance they are invoked on! You must store and use the new {@link WebElementFinder} instance returned by these methods. This
 * makes {@link WebElementFinder}s thread-safe and safe to store as {@code static final} constants.
 * </p>
 * <p>
 * This class follows the same mechanisms as {@link FormInputHandler}.
 * </p>
 * <string>Usage Example:</strong>
 * 
 * <pre>
 * WebElement element = WebElementFinder.create().by(By.id(&quot;someId&quot;)).displayed(true).webDriver(webDriver).find();
 * </pre>
 * 
 * @see FormInputHandler
 * @author rnaegele
 */
public final class WebElementFinder {

	private static final List<Class<? extends Throwable>> IGNORED_EXCEPTIONS = ImmutableList.<Class<? extends Throwable>>of(
			NotFoundException.class, WebElementException.class, StaleElementReferenceException.class);

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final WebDriver webDriver;
	private final By by;
	private final long timeoutSeconds;
	private final long sleepMillis;
	private final Boolean enabled;
	private final Boolean displayed;
	private final Boolean selected;
	private final Predicate<WebElement> condition;
	private final boolean noLogging;

	private WebElementFinder(final WebDriver webDriver, final By by, final long timeoutSeconds, final long sleepMillis,
			final Boolean enabled, final Boolean displayed, final Boolean selected, final Predicate<WebElement> condition,
			final boolean noLogging) {
		this.webDriver = webDriver;
		this.by = by;
		this.timeoutSeconds = timeoutSeconds;
		this.sleepMillis = sleepMillis;
		this.enabled = enabled;
		this.displayed = displayed;
		this.selected = selected;
		this.condition = condition;
		this.noLogging = noLogging;
	}

	private WebElementFinder(final Fields fields) {
		this(fields.webDriver, fields.by, fields.timeoutSeconds, fields.sleepMillis, fields.enabled, fields.displayed,
				fields.selected, fields.condition, fields.noLogging);
	}

	/**
	 * Creates a {@link WebElementFinder}.
	 * 
	 * @return the new {@link WebElementFinder} instance
	 */
	public static WebElementFinder create() {
		return new WebElementFinder(null, null, 0L, 0L, null, null, null, null, false);
	}

	/**
	 * Creates an new {@link WebElementFinder} based on this {@link WebElementFinder} using the specified timeout.
	 * 
	 * @param theTimeoutSeconds
	 *            the timeout in seconds for the internal {@link WebDriverWait}
	 * @return the new {@link WebElementFinder} instance
	 */
	public WebElementFinder timeout(final long theTimeoutSeconds) {
		checkArgument(theTimeoutSeconds >= 0, "'theTimeoutSeconds' must be greater than or equal to zero");
		Fields fields = new Fields(this);
		fields.timeoutSeconds = theTimeoutSeconds;
		return new WebElementFinder(fields);
	}

	/**
	 * Creates an new {@link WebElementFinder} based on this {@link WebElementFinder} using the specified timeout.
	 * 
	 * @param theTimeoutSeconds
	 *            the timeout in seconds for the internal {@link WebDriverWait}
	 * @param theSleepMillis
	 *            the sleep time in milliseconds for the internal {@link WebDriverWait}
	 * @return the {@link WebElementFinder} instance
	 */
	public WebElementFinder timeout(final long theTimeoutSeconds, final long theSleepMillis) {
		checkArgument(theTimeoutSeconds >= 0, "'theTimeoutSeconds' must be greater than or equal to zero");
		checkArgument(theSleepMillis >= 0, "'theSleepMillis' must be greater than or equal to zero");
		Fields fields = new Fields(this);
		fields.timeoutSeconds = theTimeoutSeconds;
		fields.sleepMillis = theSleepMillis;
		return new WebElementFinder(fields);
	}

	/**
	 * Creates an new {@link WebElementFinder} based on this {@link WebElementFinder} restricting the enabled status of elements.
	 * 
	 * @param theEnabled
	 *            {@code true} if elements must be enabled, {@code false} if elements must not be enabled
	 * @return the new {@link WebElementFinder} instance
	 * @see WebElement#isEnabled()
	 */
	public WebElementFinder enabled(final Boolean theEnabled) {
		Fields fields = new Fields(this);
		fields.enabled = theEnabled;
		return new WebElementFinder(fields);
	}

	/**
	 * Creates an new {@link WebElementFinder} based on this {@link WebElementFinder} restricting the displayed status of
	 * elements.
	 * 
	 * @param theDisplayed
	 *            {@code true} if elements must be displayed, {@code false} if elements must not be displayed
	 * @return the new {@link WebElementFinder} instance
	 * @see WebElement#isDisplayed()
	 */
	public WebElementFinder displayed(final Boolean theDisplayed) {
		Fields fields = new Fields(this);
		fields.displayed = theDisplayed;
		return new WebElementFinder(fields);
	}

	/**
	 * Creates an new {@link WebElementFinder} based on this {@link WebElementFinder} restricting the selected status of elements.
	 * This method should only be called if elements are indeed selectable, such as checkboxes, options in a select, and radio
	 * buttons.
	 * 
	 * @param theSelected
	 *            {@code true} if elements must be selected, {@code false} if elements must not be selected
	 * @return the new {@link WebElementFinder} instance
	 * @see WebElement#isSelected()
	 */
	public WebElementFinder selected(final Boolean theSelected) {
		Fields fields = new Fields(this);
		fields.selected = theSelected;
		return new WebElementFinder(fields);
	}

	/**
	 * Creates an new {@link WebElementFinder} based on this {@link WebElementFinder} specifying a condition elements must meet.
	 * 
	 * @param theCondition
	 *            the condition
	 * @return the new {@link WebElementFinder} instance
	 */
	public WebElementFinder condition(final Predicate<WebElement> theCondition) {
		Fields fields = new Fields(this);
		fields.condition = theCondition;
		return new WebElementFinder(fields);
	}

	/**
	 * Creates a new {@link WebElementFinder} based on this {@link WebElementFinder} using the specified element locator.
	 * 
	 * @param theBy
	 *            locates the element to operate on
	 * @return the new {@link FormInputHandler} instance
	 */
	public WebElementFinder by(final By theBy) {
		Fields fields = new Fields(this);
		fields.by = theBy;
		return new WebElementFinder(fields);
	}

	/**
	 * Creates a new {@link WebElementFinder} based on this {@link WebElementFinder} using the specified {@link WebDriver}.
	 * 
	 * @param theWebDriver
	 *            the {@link WebDriver} to use
	 * @return the new {@link FormInputHandler} instance
	 */
	public WebElementFinder webDriver(final WebDriver theWebDriver) {
		Fields fields = new Fields(this);
		fields.webDriver = theWebDriver;
		return new WebElementFinder(fields);
	}

	/**
	 * Creates a new {@link WebElementFinder} based on this {@link WebElementFinder} with logging disabled. This is useful when a
	 * {@link WebElementFinder} is using by a {@link FormInputHandler} which logs on its own.
	 * 
	 * @param theNoLogging
	 *            the {@link WebDriver} to use
	 * @return the new {@link FormInputHandler} instance
	 */
	public WebElementFinder noLogging(final boolean theNoLogging) {
		Fields fields = new Fields(this);
		fields.noLogging = theNoLogging;
		return new WebElementFinder(fields);
	}

	/**
	 * Finds the first element.
	 * 
	 * @return the element
	 */
	public WebElement find() {
		checkState(webDriver != null, "No WebDriver specified.");
		checkState(by != null, "No By instance for locating elements specified.");

		if (!noLogging) {
			log.info(toString());
		}

		WebElement element;

		if (timeoutSeconds > 0L) {
			WebDriverWait wait = createWebDriverWait();
			element = wait.until(new Function<WebDriver, WebElement>() {
				@Override
				public WebElement apply(final WebDriver input) {
					WebElement el = input.findElement(by);
					checkElement(el);
					if (condition != null && !condition.apply(el)) {
						throw new WebElementException(String.format("Condition not met for element %s: %s", el, condition));
					}
					return el;
				}

				@Override
				public String toString() {
					return WebElementFinder.this.toString();
				}
			});
		} else {
			element = webDriver.findElement(by);
			checkElement(element);
			if (condition != null && !condition.apply(element)) {
				throw new WebElementException(String.format("Condition not met for element %s: %s", element, condition));
			}
		}

		return element;
	}

	/**
	 * Finds all elements.
	 * 
	 * @return the list of elements
	 */
	public List<WebElement> findAll() {
		checkState(webDriver != null, "No WebDriver specified.");
		checkState(by != null, "No By instance for locating elements specified.");

		log.info(toString());

		final List<WebElement> result = newArrayList();

		try {
			if (timeoutSeconds > 0L) {
				WebDriverWait wait = createWebDriverWait();
				wait.until(new Function<WebDriver, List<WebElement>>() {
					@Override
					public List<WebElement> apply(final WebDriver input) {
						doFindElements(result, input);
						if (result.isEmpty()) {
							// this means, we try again until the timeout occurs
							throw new WebElementException("No matching element found.");
						}
						return result;
					}

					@Override
					public String toString() {
						return WebElementFinder.this.toString();
					}
				});
			} else {
				doFindElements(result, webDriver);
			}
			return result;
		} catch (TimeoutException ex) {
			Throwable cause = ex.getCause();
			for (Class<? extends Throwable> thClass : IGNORED_EXCEPTIONS) {
				if (thClass.isInstance(cause)) {
					return ImmutableList.of();
				}
			}
			throw new WebElementException(ex);
		}
	}

	private void doFindElements(final List<WebElement> result, final WebDriver searchContext) {
		List<WebElement> elList = searchContext.findElements(by);
		for (WebElement element : elList) {
			if (!checkElementForList(element)) {
				continue;
			}
			if (condition != null && !condition.apply(element)) {
				continue;
			}
			result.add(element);
		}
	}

	private boolean checkElementForList(final WebElement element) {
		if (enabled != null) {
			if (enabled != element.isEnabled()) {
				return false;
			}
		}
		if (displayed != null) {
			if (displayed != element.isDisplayed()) {
				return false;
			}
		}
		if (selected != null) {
			if (selected != element.isSelected()) {
				return false;
			}
		}
		return true;
	}

	private void checkElement(final WebElement element) {
		if (enabled != null) {
			if (enabled) {
				checkElementState(element, element.isEnabled(), "Element '%s' was expected to be enabled but is not.");
			} else {
				checkElementState(element, !element.isEnabled(), "Element '%s' was expected to not be enabled but is.");
			}
		}
		if (displayed != null) {
			if (displayed) {
				checkElementState(element, element.isDisplayed(), "Element '%s' was expected to be displayed but is not.");
			} else {
				checkElementState(element, !element.isDisplayed(), "Element '%s' was expected to not be displayed but is.");
			}
		}
		if (selected != null) {
			if (selected) {
				checkElementState(element, element.isSelected(), "Element '%s' was expected to be selected but is not.");
			} else {
				checkElementState(element, !element.isSelected(), "Element '%s' was expected to not be selected but is.");
			}
		}
	}

	private static void checkElementState(final WebElement element, final boolean expression, final String msgTemplate) {
		if (!expression) {
			WebElement underlyingElement;
			try {
				Method m = element.getClass().getMethod("getWrappedElement");
				m.setAccessible(true);
				underlyingElement = (WebElement) m.invoke(element);
			} catch (Exception ex) {
				underlyingElement = element;
			}
			throw new WebElementException(String.format(msgTemplate, underlyingElement));
		}
	}

	private WebDriverWait createWebDriverWait() {
		WebDriverWait webDriverWait = sleepMillis > 0L
				? new WebDriverWait(webDriver, timeoutSeconds, sleepMillis)
				: new WebDriverWait(webDriver, timeoutSeconds);
		webDriverWait.ignoreAll(IGNORED_EXCEPTIONS);
		return webDriverWait;
	}

	/**
	 * @return the webDriver
	 */
	public WebDriver getWebDriver() {
		return webDriver;
	}

	/**
	 * @return the by
	 */
	public By getBy() {
		return by;
	}

	/**
	 * @return the timeoutSeconds
	 */
	public long getTimeoutSeconds() {
		return timeoutSeconds;
	}

	/**
	 * @return the sleepMillis
	 */
	public long getSleepMillis() {
		return sleepMillis;
	}

	/**
	 * @return the enabled
	 */
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * @return the displayed
	 */
	public Boolean getDisplayed() {
		return displayed;
	}

	/**
	 * @return the selected
	 */
	public Boolean getSelected() {
		return selected;
	}

	/**
	 * @return the condition
	 */
	public Predicate<WebElement> getCondition() {
		return condition;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ShortToStringStyle.INSTANCE);
		tsb.append("by", by);
		if (timeoutSeconds > 0L) {
			tsb.append("timeoutSeconds", timeoutSeconds);
			if (sleepMillis > 0L) {
				tsb.append("sleepMillis", sleepMillis);
			}
		}
		if (enabled != null) {
			tsb.append("enabled", enabled);
		}
		if (displayed != null) {
			tsb.append("displayed", displayed);
		}
		if (selected != null) {
			tsb.append("selected", selected);
		}
		return tsb.toString();
	}

	private static class Fields {
		private WebDriver webDriver;
		private By by;
		private long timeoutSeconds;
		private long sleepMillis;
		private Boolean enabled;
		private Boolean displayed;
		private Boolean selected;
		private Predicate<WebElement> condition;
		private boolean noLogging;

		private Fields(final WebElementFinder finder) {
			this.webDriver = finder.webDriver;
			this.by = finder.by;
			this.timeoutSeconds = finder.timeoutSeconds;
			this.sleepMillis = finder.sleepMillis;
			this.enabled = finder.enabled;
			this.displayed = finder.displayed;
			this.selected = finder.selected;
			this.condition = finder.condition;
			this.noLogging = finder.noLogging;
		}
	}
}

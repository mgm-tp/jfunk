package com.mgmtp.jfunk.web.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Provides a fluent interface for finding elements, optionally with timeout and constraints
 * (enabled, displayed, selected).
 * 
 * <pre>
 * 
 * import static com.mgmtp.jfunk.web.util.WebElementFinder.with
 * ...
 * 
 * WebDriver webDriver = ...
 * By by = ...
 * 
 * WebElement element = with(webDriver).find(by);
 * element = with(webDriver).timeout(10L).find(by);
 * element = with(webDriver).selected(true).find(by);
 * List<WebElement> elements = with(webDriver).enabled(true).displayed(true).findAll(by);
 * 
 * </pre>
 * 
 * @author rnaegele
 * @version $Id$
 */
public final class WebElementFinder {

	private final WebDriver webDriver;

	private Long timeout;
	private Long sleep;
	private Boolean enabled;
	private Boolean displayed;
	private Boolean selected;
	private Predicate<WebElement> condition;

	private WebElementFinder(final WebDriver webDriver) {
		this.webDriver = webDriver;
	}

	/**
	 * Creates an {@link WebElementFinder} with the specified {@link WebDriver}.
	 * 
	 * @param webDriver
	 *            the WebDriver
	 * @return the {@link WebElementFinder} instance
	 */
	public static WebElementFinder with(final WebDriver webDriver) {
		return new WebElementFinder(webDriver);
	}

	/**
	 * Creates an {@link WebElementFinder} with the specified {@link WebDriver} and a timeout to be
	 * used.
	 * 
	 * @param timeoutSeconds
	 *            the timeout in seconds for the internal {@link WebDriverWait}
	 * @return the {@link WebElementFinder} instance
	 */
	public WebElementFinder timeout(final long timeoutSeconds) {
		checkArgument(timeoutSeconds > 0, "'timeoutSeconds' must be greater than zero");
		this.timeout = timeoutSeconds;
		return this;
	}

	/**
	 * Creates an {@link WebElementFinder} with the specified {@link WebDriver} and a timeout to be
	 * used.
	 * 
	 * @param timeoutSeconds
	 *            the timeout in seconds for the internal {@link WebDriverWait}
	 * @param sleepMillis
	 *            the sleep time in milliseconds for the internal {@link WebDriverWait}
	 * @return the {@link WebElementFinder} instance
	 */
	public WebElementFinder timeout(final long timeoutSeconds, final long sleepMillis) {
		checkArgument(timeoutSeconds > 0, "'timeoutSeconds' must be greater than zero");
		checkArgument(sleepMillis > 0, "'sleepMillis' must be greater than zero");
		this.timeout = timeoutSeconds;
		this.sleep = sleepMillis;
		return this;
	}

	/**
	 * Specifies that a {@link WebElement} either must or must not be enabled. By default, returned
	 * element may or may not be enabled.
	 * 
	 * @param flag
	 *            {@code true} if elements must be enabled, {@code false} if elements must not be
	 *            enabled
	 * @return the {@link WebElementFinder} instance
	 * @see WebElement#isEnabled()
	 */
	public WebElementFinder enabled(final Boolean flag) {
		this.enabled = flag;
		return this;
	}

	/**
	 * Specifies that a {@link WebElement} either must or must not be displayed. By default,
	 * returned element may or may not be displayed.
	 * 
	 * @param flag
	 *            {@code true} if elements must be displayed, {@code false} if elements must not be
	 *            displayed
	 * @return the {@link WebElementFinder} instance
	 * @see WebElement#isDisplayed()
	 */
	public WebElementFinder displayed(final Boolean flag) {
		this.displayed = flag;
		return this;
	}

	/**
	 * Specifies that a {@link WebElement} either must or must not be selected. By default, returned
	 * element may or may not be selected. This method should only be called if elements are indeed
	 * selectable, such as checkboxes, options in a select and radio buttons.
	 * 
	 * @param flag
	 *            {@code true} if elements must be selected, {@code false} if elements must not be
	 *            selected
	 * @return the {@link WebElementFinder} instance
	 * @see WebElement#isSelected()
	 */
	public WebElementFinder selected(final Boolean flag) {
		this.selected = flag;
		return this;
	}

	/**
	 * Specifies a condition elements must meet.
	 * 
	 * @param theCondition
	 *            the condition
	 * 
	 * @return the {@link WebElementFinder} instance
	 */
	public WebElementFinder condition(final Predicate<WebElement> theCondition) {
		this.condition = theCondition;
		return this;
	}

	/**
	 * Finds the first element.
	 * 
	 * @param by
	 *            the element locator
	 * @return the element
	 */
	public WebElement find(final By by) {
		WebElement element;

		if (timeout != null) {
			WebDriverWait wait = createWebDriverWait();
			element = wait.until(new Function<WebDriver, WebElement>() {
				@Override
				public WebElement apply(final WebDriver input) {
					WebElement el = input.findElement(by);
					if (condition != null) {
						if (!condition.apply(el)) {
							throw new WebElementException(el, String.format("Condition not met for element %s: %s", el, condition));
						}
					}
					return el;
				}
			});
		} else {
			element = webDriver.findElement(by);
			if (condition != null) {
				if (!condition.apply(element)) {
					throw new WebElementException(element, String.format("Condition not met for element %s: %s", element, condition));
				}
			}

		}

		checkElement(element);
		return element;
	}

	/**
	 * Finds the elements.
	 * 
	 * @param by
	 *            the element locator
	 * @return the list of elements
	 */
	public List<WebElement> findAll(final By by) {
		List<WebElement> elements;

		if (timeout != null) {
			WebDriverWait wait = createWebDriverWait();
			elements = wait.until(new Function<WebDriver, List<WebElement>>() {
				@Override
				public List<WebElement> apply(final WebDriver input) {
					List<WebElement> elList = input.findElements(by);
					if (condition != null) {
						for (WebElement el : elList) {
							if (!condition.apply(el)) {
								throw new WebElementException(el, String.format("Condition not met for element %s: %s", el, condition));
							}
						}
					}
					return elList;
				}
			});
		} else {
			elements = webDriver.findElements(by);
			if (condition != null) {
				for (WebElement el : elements) {
					if (!condition.apply(el)) {
						throw new WebElementException(el, String.format("Condition not met for element %s: %s", el, condition));
					}
				}
			}
		}

		for (WebElement element : elements) {
			checkElement(element);
		}
		return elements;
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
			throw new WebElementException(element, String.format(msgTemplate, element));
		}
	}

	private WebDriverWait createWebDriverWait() {
		WebDriverWait webDriverWait = sleep != null
				? new WebDriverWait(webDriver, timeout, sleep)
				: new WebDriverWait(webDriver, timeout);
		if (condition != null) {
			webDriverWait.ignoring(NotFoundException.class, WebElementException.class);
		} else {
			webDriverWait.ignoring(NotFoundException.class);
		}
		return webDriverWait;
	}
}

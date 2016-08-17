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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets.SetView;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.common.util.JFunkUtils;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.web.WebConstants;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.difference;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 * <p>
 * Utility class for enhancing {@link WebDriver} functionality. Uses {@link WebElementFinder} and
 * {@link FormInputHandler} internally and thus can handle timeouts implicitly.
 * </p>
 * <p>
 * An script-scoped instance of this class can be retrieve via dependency injection.
 * </p>
 * <p>
 * When using {@link WebDriver} to perform certain actions (e.g., click) on elements which are
 * covered by some other elements, the action may succeed even if it would fail, if performed
 * manually by the user. By enabling the flag {@link #topmostElementCheck}, the methods listed
 * below will throw an {@link AssertionError} if the element in question is covered by some other
 * element.
 *     <ul>
 *         <li>{@link #clear(By)}</li>
 *         <li>{@link #click(By)}</li>
 *         <li>{@link #click(By, ClickSpecs)}</li>
 *         <li>{@link #contextClick(By)}</li>
 *         <li>{@link #doubleClick(By)}</li>
 *         <li>{@link #dragAndDrop(By, By)}</li>
 *         <li>{@link #hover(By)}</li>
 *         <li>{@link #openNewWindow(By, long)}</li>
 *         <li>{@link #processField(By, String)}</li>
 *         <li>{@link #processField(By, String, String)}</li>
 *         <li>{@link #processField(By, String, String, Integer)}</li>
 *         <li>{@link #sendKeys(By, CharSequence...)}</li>
 *         <li>{@link #tryClick(By)}</li>
 *     </ul>
 * </p>
 *
 * @author rnaegele
 * @since 3.1
 */
public final class WebDriverTool implements SearchContext {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverTool.class);

	// we must return the JS function result TextRectangle object as a map here,
	// as for IE the TextRectangle object cannot be cast to a map
	private static final String JS_GET_BOUNDING_CLIENT_RECT =
			"return {top:arguments[0].getBoundingClientRect().top,"
			+ "left:arguments[0].getBoundingClientRect().left,"
			+ "bottom:arguments[0].getBoundingClientRect().bottom,"
			+ "right:arguments[0].getBoundingClientRect().right,"
			+ "width:arguments[0].getBoundingClientRect().width,"
			+ "height:arguments[0].getBoundingClientRect().height};";

	private static final String JS_ELEMENT_FROM_POINT =
			"return document.elementFromPoint(arguments[0], arguments[1]);";

	// jQuery's way of obtaining the viewport
	private static final String JS_GET_VIEWPORT =
			"return {width:document.documentElement.clientWidth,"
			+ "height:document.documentElement.clientHeight};";

	private static final String JS_ELEMENT_HAS_CHILDREN =
			"return arguments[0].childElementCount > 0";

	private final WebDriver webDriver;
	private final WebElementFinder wef;
	private final FormInputHandler fih;
	private final Map<String, DataSet> dataSets;

	private boolean topmostElementCheck;

	@Inject
	WebDriverTool(final WebDriver webDriver, final WebElementFinder wef, final FormInputHandler fih,
		final Map<String, DataSet> dataSets, final Provider<Configuration> configProvider) {
		this.webDriver = webDriver;
		this.wef = wef;
		this.fih = fih;
		this.dataSets = dataSets;

		Configuration config = configProvider.get();
		String value = trimToNull(config.get(WebConstants.WDT_TOPMOST_ELEMENT_CHECK));
		if (value != null) {
			topmostElementCheck = Boolean.parseBoolean(value);
		}
	}

	public void get(final String url) {
		LOGGER.info("GET {}", url);
		webDriver.get(url);
	}

	/**
	 * Finds the first element. Uses the internal {@link WebElementFinder}.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @return the element
	 * @deprecated Use {@link #findElement(By)} instead
	 */
	@Deprecated
	public WebElement find(final By by) {
		return findElement(by);
	}

	/**
	 * Finds the first element. Uses the internal {@link WebElementFinder}.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @return the element
	 */
	@Override
	public WebElement findElement(final By by) {
		return wef.by(by).find();
	}

	/**
	 * Finds the first element. Uses the internal {@link WebElementFinder}, which tries to apply
	 * the specified {@code condition} until it times out.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @param condition
	 *            a condition the found element must meet
	 * @return the element
	 * @deprecated Use {@link #findElement(By, Predicate)} instead
	 */
	@Deprecated
	public WebElement find(final By by, final Predicate<WebElement> condition) {
		return findElement(by, condition);
	}

	/**
	 * Finds the first element. Uses the internal {@link WebElementFinder}, which tries to apply
	 * the specified {@code condition} until it times out.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @param condition
	 *            a condition the found element must meet
	 * @return the element
	 */
	public WebElement findElement(final By by, final Predicate<WebElement> condition) {
		return wef.by(by).condition(condition).find();
	}

	/**
	 * Finds all elements. Uses the internal {@link WebElementFinder}.
	 *
	 * @param by
	 *            the {@link By} used to locate the elements
	 * @return the list of elements
	 * @deprecated Use {@link #findElements(By)} instead
	 */
	@Deprecated
	public List<WebElement> findAll(final By by) {
		return findElements(by);
	}

	/**
	 * Finds all elements. Uses the internal {@link WebElementFinder}.
	 *
	 * @param by
	 *            the {@link By} used to locate the elements
	 * @return the list of elements
	 */
	@Override
	public List<WebElement> findElements(final By by) {
		return wef.by(by).findAll();
	}

	/**
	 * Finds all elements. Uses the internal {@link WebElementFinder}, which tries to apply the
	 * specified {@code condition} until it times out.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @param condition
	 *            a condition the found elements must meet
	 * @return the list of elements
	 * @deprecated Use {@link #findElements(By, Predicate)} instead
	 */
	@Deprecated
	public List<WebElement> findAll(final By by, final Predicate<WebElement> condition) {
		return findElements(by, condition);
	}

	/**
	 * Finds all elements. Uses the internal {@link WebElementFinder}, which tries to apply the
	 * specified {@code condition} until it times out.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @param condition
	 *            a condition the found elements must meet
	 * @return the list of elements
	 */
	public List<WebElement> findElements(final By by, final Predicate<WebElement> condition) {
		return wef.by(by).condition(condition).findAll();
	}

	/**
	 * Performs a drag'n'drop operation of the element located by {@code sourceBy} to the
	 * location of the element located by {@code targetBy}.
	 *
	 * @param sourceBy
	 *            the {@link By} used to locate the source element
	 * @param targetBy
	 *            the {@link By} used to locate the element representing the target location
	 */
	public void dragAndDrop(final By sourceBy, final By targetBy) {
		checkTopmostElement(sourceBy);
		checkTopmostElement(targetBy);
		WebElement source = findElement(sourceBy);
		WebElement target = findElement(targetBy);
		new Actions(webDriver).dragAndDrop(source, target).perform();
	}

	/**
	 * Repeatedly applies the current {@link WebDriver} instance to the specified function until
	 * one of the following occurs:
	 * <ol>
	 * <li>the function returns neither null nor false,</li>
	 * <li>the function throws an unignored exception,</li>
	 * <li>the timeout expires,
	 * <li>the current thread is interrupted</li>
	 * </ol>
	 *
	 * @param function
	 *            the function
	 * @param <V>
	 *            the function's expected return type
	 * @return the function's return value if the function returned something different from
	 *         null or false before the timeout expired
	 * @throws TimeoutException
	 *             if the timeout expires.
	 */
	public <V> V waitFor(final Function<? super WebDriver, V> function) {
		return newWebDriverWait().until(function);
	}

	/**
	 * Repeatedly applies the current {@link WebDriver} instance to the specified function until
	 * one of the following occurs:
	 * <ol>
	 * <li>the function returns neither null nor false,</li>
	 * <li>the function throws an unignored exception,</li>
	 * <li>the timeout expires,
	 * <li>the current thread is interrupted</li>
	 * </ol>
	 *
	 * @param function
	 *            the function
	 * @param timeoutSeconds
	 *            the timeout in seconds
	 * @param <V>
	 *            the function's expected return type
	 * @return the function's return value if the function returned something different from
	 *         null or false before the timeout expired
	 * @throws TimeoutException
	 *             if the timeout expires.
	 */
	public <V> V waitFor(final Function<? super WebDriver, V> function, final long timeoutSeconds) {
		return newWebDriverWait(timeoutSeconds).until(function);
	}

	/**
	 * Repeatedly applies the current {@link WebDriver} instance to the specifed function until
	 * one of the following occurs:
	 * <ol>
	 * <li>the function returns neither null nor false,</li>
	 * <li>the function throws an unignored exception,</li>
	 * <li>the timeout expires,
	 * <li>the current thread is interrupted</li>
	 * </ol>
	 * The method uses the same timeout and milliseconds to sleep between polls as the
	 * internally used default {@link WebElementFinder} instance
	 *
	 * @param function
	 *            the function
	 * @param timeoutSeconds
	 *            the timeout in seconds
	 * @param sleepMillis
	 *            the time in milliseconds to sleep between polls
	 * @param <V>
	 *            the function's expected return type
	 * @return the function's return value if the function returned something different from
	 *         null or false before the timeout expired
	 * @throws TimeoutException
	 *             if the timeout expires.
	 */
	public <V> V waitFor(final Function<? super WebDriver, V> function, final long timeoutSeconds, final long sleepMillis) {
		return newWebDriverWait(timeoutSeconds, sleepMillis).until(function);
	}

	/**
	 * Repeatedly applies the current {@link WebDriver} instance to the specified predicate
	 * until the timeout expires or the predicate evaluates to true.
	 *
	 * @param predicate
	 *            the predicate to wait on
	 * @throws TimeoutException
	 *             if the timeout expires.
	 */
	public void waitFor(final Predicate<WebDriver> predicate) {
		newWebDriverWait().until(predicate);
	}

	/**
	 * Repeatedly applies the current {@link WebDriver} instance to the specified predicate
	 * until the timeout expires or the predicate evaluates to true.
	 *
	 * @param predicate
	 *            the predicate to wait on
	 * @param timeoutSeconds
	 *            the timeout in seconds
	 * @throws TimeoutException
	 *             if the timeout expires.
	 */
	public void waitFor(final Predicate<WebDriver> predicate, final long timeoutSeconds) {
		newWebDriverWait(timeoutSeconds).until(predicate);
	}

	/**
	 * Repeatedly applies the current {@link WebDriver} instance to the specified predicate
	 * until the timeout expires or the predicate evaluates to true.
	 *
	 * @param predicate
	 *            the predicate to wait on
	 * @param timeoutSeconds
	 *            the timeout in seconds
	 * @param sleepMillis
	 *            the time in milliseconds to sleep between polls
	 * @throws TimeoutException
	 *             if the timeout expires.
	 */
	public void waitFor(final Predicate<WebDriver> predicate, final long timeoutSeconds, final long sleepMillis) {
		newWebDriverWait(timeoutSeconds, sleepMillis).until(predicate);
	}

	/**
	 * Creates a new {@link WebDriverWait} with the same timeout and milliseconds to sleep
	 * between polls as the internally used default {@link WebElementFinder} instance.
	 *
	 * @return the newly created {@link WebDriverWait} instance
	 */
	public WebDriverWait newWebDriverWait() {
		return wef.getSleepMillis() > 0L
			? newWebDriverWait(wef.getTimeoutSeconds(), wef.getSleepMillis())
				: newWebDriverWait(wef.getTimeoutSeconds());
	}

	/**
	 * Creates a new {@link WebDriverWait} with the specified timeout.
	 *
	 * @param timeoutSeconds
	 *            the timeout in seconds
	 * @return the newly created {@link WebDriverWait} instance
	 */
	public WebDriverWait newWebDriverWait(final long timeoutSeconds) {
		return new LoggingWebDriverWait(webDriver, timeoutSeconds);
	}

	/**
	 * Creates a new {@link WebDriverWait} with the specified timeout and milliseconds to sleep
	 * between polls.
	 *
	 * @param timeoutSeconds
	 *            the timeout in seconds
	 * @param sleepMillis
	 *            the time in milliseconds to sleep between polls
	 * @return the newly created {@link WebDriverWait} instance
	 */
	public WebDriverWait newWebDriverWait(final long timeoutSeconds, final long sleepMillis) {
		return new LoggingWebDriverWait(webDriver, timeoutSeconds, sleepMillis);
	}

	/**
	 * Tries to find and element and clicks on it if found. Uses a timeout of two seconds.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @return {@code true} if the element was found and clicked, {@code false} otherwise
	 */
	public boolean tryClick(final By by) {
		LOGGER.info("Trying to click on {}", by);
		List<WebElement> elements = wef.timeout(2L).by(by).findAll();
		if (elements.size() > 0) {
			WebElement element = elements.get(0);
			checkTopmostElement(by);
			new Actions(webDriver).moveToElement(element).click().perform();
			LOGGER.info("Click successful");
			return true;
		}
		LOGGER.info("Click not successful");
		return false;
	}

	/**
	 * Delegates to {@link #findElement(By)} and calls
	 * {@link WebElement#sendKeys(CharSequence...) sendKeys(CharSequence...)} on the returned
	 * element.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @param keysToSend
	 *            the keys to send
	 */
	public void sendKeys(final By by, final CharSequence... keysToSend) {
		checkTopmostElement(by);
		findElement(by).sendKeys(keysToSend);
	}

	/**
	 * Delegates to {@link #findElement(By)} and calls {@link WebElement#clear() clear()} on the
	 * returned element.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 */
	public void clear(final By by) {
		checkTopmostElement(by);
		findElement(by).clear();
	}

	/**
	 * Delegates to {@link #findElement(By)} and performs the click using
	 * {@link Actions#click(WebElement)}.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 */
	public void click(final By by) {
		checkTopmostElement(by);
		WebElement element = findElement(by);
		new Actions(webDriver).moveToElement(element).click().perform();
	}

	/**
	 * Delegates to {@link #findElement(By)}, moves the mouse to the offset specified by
	 * {@code clickPoint} using {@link Actions#moveToElement(WebElement, int, int)}, and
	 * performs the click using {@link Actions#click()}.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @param clickSpecs
	 *            specifies an offset where to click within the bounds of the element
	 */
	public void click(final By by, final ClickSpecs clickSpecs) {
		checkTopmostElement(by);
		WebElement element = findElement(by);
		Rectangle rect = getBoundingClientRect(by);
		Point p = clickSpecs.getPoint(rect);

		LOGGER.info("Clicking on {} at offset: {}(x={}, y={})", by, clickSpecs, p.x, p.y);
		new Actions(webDriver).moveToElement(element, p.x, p.y).click().perform();
	}

	/**
	 * Delegates to {@link #findElement(By)} and then performs a context-click using the
	 * {@link Actions} class.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 */
	public void contextClick(final By by) {
		checkTopmostElement(by);
		WebElement element = findElement(by);
		new Actions(webDriver).contextClick(element).perform();
	}

	/**
	 * Delegates to {@link #findElement(By)} and then performs a double-click using the
	 * {@link Actions} class.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 */
	public void doubleClick(final By by) {
		checkTopmostElement(by);
		WebElement element = findElement(by);
		new Actions(webDriver).doubleClick(element).perform();
	}

	/**
	 * Delegates to {@link #findElement(By)} and then moves the mouse to the returned element
	 * using the {@link Actions} class.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 */
	public void hover(final By by) {
		checkTopmostElement(by);
		WebElement element = findElement(by);
		new Actions(webDriver).moveToElement(element).perform();
	}

	/**
	 * Delegates to {@link #findElement(By)}, moves the mouse to the returned element using the
	 * {@link Actions} class and then tries to find and element using {@code byToAppear}.
	 * Default timeouts are applied.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @param byToAppear
	 *            the {@link By} used to locate the element that is supposed to appear after
	 *            hovering
	 */
	public WebElement hover(final By by, final By byToAppear) {
		final WebElementFinder finder = wef.timeout(2L, 200L).by(byToAppear);

		return waitFor((ExpectedCondition<WebElement>) input -> {
			WebElement element = finder.by(by).find();
			checkTopmostElement(by);
			new Actions(webDriver).moveToElement(element).perform();
			return finder.by(byToAppear).find();
		});
	}

	/**
	 * Delegates to {@link #findElement(By)} and then calls
	 * {@link WebElement#getAttribute(String) getAttribute(String)} on the returned element.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @param attributeName
	 *            the attribute name
	 * @return the attribute value
	 */
	public String getAttributeValue(final By by, final String attributeName) {
		WebElement element = findElement(by);
		return element.getAttribute(attributeName);
	}

	/**
	 * Delegates to {@link #findElement(By)} and then calls
	 * {@link WebElement#getCssValue(String) getAttribute(String)} on the returned element.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @param propertyName
	 *            the name of the CSS property
	 * @return The current, computed value of the property.
	 */
	public String getCssValue(final By by, final String propertyName) {
		WebElement element = findElement(by);
		return element.getCssValue(propertyName);
	}

	/**
	 * Delegates to {@link #findElement(By)} and then calls {@link WebElement#getText()
	 * getText()} on the returned element. The element's text is passed to
	 * {@link JFunkUtils#normalizeSpace(String)}.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @return the text
	 */
	public String getElementText(final By by) {
		return getElementText(by, true);
	}

	/**
	 * Delegates to {@link #findElement(By)} and then calls {@link WebElement#getText()
	 * getText()} on the returned element. If {@code normalizeSpace} is {@code true}, the
	 * element's text is passed to {@link JFunkUtils#normalizeSpace(String)}.
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @param normalizeSpace
	 *            specifies whether whitespace in the element text are to be normalized
	 * @return the text
	 */
	public String getElementText(final By by, final boolean normalizeSpace) {
		WebElement element = findElement(by);
		String text = element.getText();
		return normalizeSpace ? JFunkUtils.normalizeSpace(text) : text;
	}

	/**
	 * <p>
	 * Delegates to {@link #findElement(By)} and then calls
	 * {@link WebElement#getAttribute(String) getAttribute("innerText")} on the returned
	 * element. The element's text is passed to {@link JFunkUtils#normalizeSpace(String)}.
	 * </p>
	 * <p>
	 * The difference to {@link #getElementText(By)} is that this method returns the complete
	 * inner text of the element, not only the visible (i. e. not hidden by CSS) one.
	 * </p>
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @return the text
	 */
	public String getInnerText(final By by) {
		return getInnerText(by, true);
	}

	/**
	 * <p>
	 * Delegates to {@link #findElement(By)} and then calls
	 * {@link WebElement#getAttribute(String) getAttribute("innerText")} on the returned
	 * element. If {@code normalizeSpace} is {@code true} , the element's text is passed to
	 * {@link JFunkUtils#normalizeSpace(String)}.
	 * </p>
	 * <p>
	 * The difference to {@link #getElementText(By, boolean)} is that this method returns the
	 * complete inner text of the element, not only the visible (i. e. not hidden by CSS) one.
	 * </p>
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @param normalizeSpace
	 *            specifies whether whitespace in the element text are to be normalized
	 * @return the text
	 */
	public String getInnerText(final By by, final boolean normalizeSpace) {
		WebElement element = findElement(by);
		String text = element.getAttribute("innerText");
		return normalizeSpace ? JFunkUtils.normalizeSpace(text) : text;
	}

	/**
	 * Uses the internal {@link FormInputHandler} to set a form field.
	 *
	 * @param by
	 *            the {@link By} used to locate the element representing an HTML input or
	 *            textarea
	 * @param dataSetKey
	 *            the data set key
	 * @param dataKey
	 *            the key used to retrieve the value for the field from the data set with the
	 *            specifies data set key
	 */
	public void processField(final By by, final String dataSetKey, final String dataKey) {
		checkTopmostElement(by);
		fih.by(by).dataSet(dataSets.get(dataSetKey)).dataKey(dataKey).perform();
	}

	/**
	 * Uses the internal {@link FormInputHandler} to set an indexed form field.
	 *
	 * @param by
	 *            the {@link By} used to locate the element representing an HTML input or
	 *            textarea
	 * @param dataSetKey
	 *            the data set key
	 * @param dataKey
	 *            the key used to retrieve the value for the field from the data set with the
	 *            specifies data set key
	 * @param dataIndex
	 *            the index for looking up dat value in the data set
	 */
	public void processField(final By by, final String dataSetKey, final String dataKey, final Integer dataIndex) {
		checkTopmostElement(by);
		fih.by(by).dataSet(dataSets.get(dataSetKey)).dataKeyWithIndex(dataKey, dataIndex).perform();
	}

	/**
	 * Uses the internal {@link FormInputHandler} to set form field. This method does not use a
	 * data set to retrieve the value.
	 *
	 * @param by
	 *            the {@link By} used to locate the element representing an HTML input or
	 *            textarea
	 * @param value
	 *            the value to set the field to
	 */
	public void processField(final By by, final String value) {
		checkTopmostElement(by);
		fih.by(by).value(value).perform();
	}

	/**
	 * Returns the size of an element and its position relative to the viewport. Uses JavaScript
	 * calling Element.getBoundingClientRect().
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @return the rectangle
	 */
	public Rectangle getBoundingClientRect(final By by) {
		WebElement el = findElement(by);

		@SuppressWarnings("unchecked")
		Map<String, Number> result = (Map<String, Number>) executeScript(JS_GET_BOUNDING_CLIENT_RECT, el);
		Rectangle rectangle = new Rectangle(result.get("top").intValue(), result.get("left").intValue(),
			result.get("bottom").intValue(), result.get("right").intValue(), result.get("width").intValue(),
			result.get("height").intValue());

		LOGGER.info("Bounding client rect for {}: {}", by, rectangle);
		return rectangle;
	}

	/**
	 * Returns the size of the viewport excluding, if rendered, the vertical and horizontal scrollbars.
	 * Uses JavaScript calling document.documentElement.client[Width|Height]().
	 *
	 * @return the rectangle
	 */
	public Rectangle getViewport() {
		@SuppressWarnings("unchecked")
		Map<String, Number> result = (Map<String, Number>) executeScript(JS_GET_VIEWPORT);
		int width = result.get("width").intValue();
		int height = result.get("height").intValue();
		Rectangle viewport = new Rectangle(0, 0, height, width, width, height);
		LOGGER.info("Viewport rectangle: {}", viewport);
		return viewport;
	}

	/**
	 * Returns the topmost element at the specified coordinates. Uses JavaScript calling
	 * Document.elementFromPoint().
	 *
	 * @param x
	 *            the horizontal position within the current viewport
	 * @param y
	 *            the vertical position within the current viewport
	 * @return the topmost element at the given coordinates
	 */
	public WebElement elementFromPoint(final int x, final int y) {
		return (WebElement) executeScript(JS_ELEMENT_FROM_POINT, x, y);
	}

	/**
	 * Execute JavaScript in the context of the currently selected frame or window.
	 *
	 * @see JavascriptExecutor#executeScript(String, Object...)
	 * @param script
	 *            The JavaScript to execute
	 * @param args
	 *            The arguments to the script. May be empty
	 * @return One of Boolean, Long, String, List or WebElement. Or null.
	 */
	public Object executeScript(final String script, final Object... args) {
		LOGGER.info("executeScript: {}", new ToStringBuilder(this, LoggingToStringStyle.INSTANCE).append("script", script).append("args", args));
		return ((JavascriptExecutor) webDriver).executeScript(script, args);
	}

	/**
	 * Execute an asynchronous piece of JavaScript in the context of the currently selected
	 * frame or window.
	 *
	 * @see JavascriptExecutor#executeAsyncScript(String, Object...)
	 * @param script
	 *            The JavaScript to execute
	 * @param args
	 *            The arguments to the script. May be empty
	 * @return One of Boolean, Long, String, List or WebElement. Or null.
	 */
	public Object executeAsyncScript(final String script, final Object... args) {
		LOGGER.info("executeAsyncScript: {}", new ToStringBuilder(this, LoggingToStringStyle.INSTANCE).append("script", script).append("args", args));
		return ((JavascriptExecutor) webDriver).executeAsyncScript(script, args);
	}

	/**
	 * Opens a new window and switches to it. The window to switch to is determined by diffing
	 * the given {@code existingWindowHandles} with the current ones. The difference must be
	 * exactly one window handle which is then used to switch to.
	 *
	 * @param openClickBy
	 *            identifies the element to click on in order to open the new window
	 * @param timeoutSeconds
	 *            the timeout in seconds to wait for the new window to open
	 * @return the handle of the window that opened the new window
	 */
	public String openNewWindow(final By openClickBy, final long timeoutSeconds) {
		checkTopmostElement(openClickBy);
		return openNewWindow(() -> sendKeys(openClickBy, Keys.chord(Keys.CONTROL, Keys.RETURN)), timeoutSeconds);
	}

	/**
	 * Opens a new window blank window using {@code CONTROL + T} and switches to it.
	 *
	 *@param timeoutSeconds
	 *            the timeout in seconds to wait for the new window to open
	 *
	 * @return the handle of the window that opened the new window
	 */
	public String openNewWindow(final long timeoutSeconds) {
		return openNewWindow(() -> findElement(By.cssSelector("body")).sendKeys(Keys.chord(Keys.CONTROL, "t")), timeoutSeconds);
	}

	/**
	 * Opens a new window, switches to it, and loads the given URL in the new window.
	 *
	 * @param url
	 *            the url to open
	 * @param timeoutSeconds
	 *            the timeout in seconds to wait for the new window to open
	 * @return the handle of the window that opened the new window
	 */
	public String openNewWindow(final String url, final long timeoutSeconds) {
		String oldHandle = openNewWindow(timeoutSeconds);
		get(url);
		return oldHandle;
	}

	/**
	 * Opens a new window and switches to it. The window to switch to is determined by diffing
	 * the given {@code existingWindowHandles} with the current ones. The difference must be
	 * exactly one window handle which is then used to switch to.
	 *
	 * @param openCommand
	 *            logic for opening the new window
	 * @param timeoutSeconds
	 *            the timeout in seconds to wait for the new window to open
	 * @return the handle of the window that opened the new window
	 */
	public String openNewWindow(final Runnable openCommand, final long timeoutSeconds) {
		String oldHandle = webDriver.getWindowHandle();
		final Set<String> existingWindowHandles = webDriver.getWindowHandles();

		openCommand.run();

		Function<WebDriver, String> function = new Function<WebDriver, String>() {
			@Override
			public String apply(final WebDriver input) {
				Set<String> newWindowHandles = webDriver.getWindowHandles();
				SetView<String> newWindows = difference(newWindowHandles, existingWindowHandles);
				if (newWindows.isEmpty()) {
					throw new NotFoundException("No new window found.");
				}
				return getOnlyElement(newWindows);
			}

			@Override
			public String toString() {
				return "new window to open";
			}
		};

		String newHandle = waitFor(function, timeoutSeconds);
		webDriver.switchTo().window(newHandle);
		return oldHandle;
	}

	/**
	 * Issues a log message before executing {@link WebDriver#switchTo()}.
	 *
	 * @return A TargetLocator which can be used to select a frame or window
	 */
	public TargetLocator switchTo() {
		LOGGER.info("Switching WebDriver...");
		return webDriver.switchTo();
	}

	/**
	 * Issues a log message before executing {@link WebDriver#close()}.
	 */
	public void close() {
		LOGGER.info("Closing window: {}", webDriver.getTitle());
		webDriver.close();
	}

	/**
	 * Asserts that the element is not covered by any other element.
	 *
	 * @param by
	 *            the {@link By} used to locate the element.
	 * @throws WebElementException
	 * 				if the element cannot be located or moved into the viewport.
	 * @throws AssertionError
	 * 				if the element is covered by some other element.
	 */
	public void assertTopmostElement(By by) {
		if (!isTopmostElementCheckApplicable(by)) {
			LOGGER.warn("The element identified by '{}' is not a leaf node in the "
					+ "document tree. Thus, it cannot be checked if the element is topmost. "
					+ "The topmost element check cannot be performed and is skipped.", by);
			return;
		}
		LOGGER.info("Checking whether the element identified by '{}' is the topmost element.", by);
		WebElement topmostElement = findTopmostElement(by);
		WebElement element = findElement(by);
		if (!element.equals(topmostElement)) {
			throw new AssertionError(format("The element '%s' identified by '%s' is covered by '%s'.",
					outerHtmlPreview(element), by, outerHtmlPreview(topmostElement)));
		}
	}

	/**
	 * Checks if an element is the topmost, i.e. not covered by any other element.
	 *
	 * @param by
	 *            the {@link By} used to locate the element.
	 * @throws WebElementException
	 * 				if the element cannot be located or moved into the viewport.
	 * @return {@code true} if the element is the topmost element, {@code false}
	 *			if not or {@code null} if the topmostElementCheck is not applicable.
	 *
	 */
	public Boolean isTopmostElement(By by) {
		if (!isTopmostElementCheckApplicable(by)) {
			return null;
		}
		WebElement topmostElement = findTopmostElement(by);
		WebElement element = findElement(by);
		return element.equals(topmostElement);
	}

	/**
	 * @return {@code true} if the topmost element check is enabled, otherwise {@code false}.
	 */
	public boolean isTopmostElementCheck() {
		return topmostElementCheck;
	}

	/**
	 * Sets the topmost element check flag to the specified value.
	 *
	 * @param topmostElementCheck
	 * 			new value.
	 */
	public void setTopmostElementCheck(boolean topmostElementCheck) {
		LOGGER.info("{}abling the topmost element check.", topmostElementCheck ? "En" : "Dis");
		this.topmostElementCheck = topmostElementCheck;
	}

	private void checkTopmostElement(By by) {
		if (topmostElementCheck) {
			assertTopmostElement(by);
		}
	}

	private boolean elementHasChildren(By by) {
		WebElement el = findElement(by);
		return (Boolean) executeScript(JS_ELEMENT_HAS_CHILDREN, el);
	}

	private boolean isTopmostElementCheckApplicable(By by) {
		return !elementHasChildren(by);
	}

	private WebElement findTopmostElement(By by) {
		Point elementsVisibleCenterPoint = getElementsVisibleRectangle(by).center();
		WebElement topmostElement = elementFromPoint(elementsVisibleCenterPoint.x, elementsVisibleCenterPoint.y);
		if (topmostElement == null) {
			throw new WebElementException(format("The element identified by '%s' is outside the viewport.", by));
		}
		return topmostElement;
	}

	private Rectangle getElementsVisibleRectangle(By by) {
		Rectangle viewport = getViewport();
		Rectangle intersection = viewport.intersection(getBoundingClientRect(by));
		if (intersection == null) {
			LOGGER.info("Scrolling the element identified by '{}' into the viewport.", by);
			new Actions(webDriver).moveToElement(findElement(by)).perform();
			intersection = viewport.intersection(getBoundingClientRect(by));
			if (intersection == null) {
				throw new WebElementException(format("The element identified by '%s' is outside the viewport.", by));
			}
		}
		return intersection;
	}

	private String outerHtmlPreview(WebElement webElement) {
		String outerHtml = webElement.getAttribute("outerHTML");
		int maxPreviewLength = 256;
		if (outerHtml.length() > maxPreviewLength) {
			outerHtml = outerHtml.substring(0, maxPreviewLength) + "...";
		}
		return outerHtml;
	}
}

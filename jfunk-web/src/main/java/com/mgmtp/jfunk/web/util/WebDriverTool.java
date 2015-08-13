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

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.difference;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.Point;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets.SetView;
import com.mgmtp.jfunk.common.util.JFunkUtils;
import com.mgmtp.jfunk.data.DataSet;

/**
 * <p>
 * Utility class for enhancing {@link WebDriver} functionality. Uses {@link WebElementFinder} and
 * {@link FormInputHandler} internally and thus can handle timeouts implicitly.
 * </p>
 * <p>
 * An script-scoped instance of this class can be retrieve via dependency injection.
 * </p>
 *
 * @author rnaegele
 * @since 3.1
 */
public class WebDriverTool implements SearchContext {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String JS_APPEND_OPEN_WINDOW_LINK = "(function() { "
		+ "var jFunkAnchorTag = document.createElement('a');"
		+ "jFunkAnchorTag.appendChild(document.createTextNode('jfunk-new-window-link'));"
		+ "jFunkAnchorTag.setAttribute('id', '%s');"
		+ "jFunkAnchorTag.setAttribute('href', '%s');"
		+ "jFunkAnchorTag.setAttribute('target', '_blank');"
		+ "jFunkAnchorTag.setAttribute('style', 'display:block; z-index: 100000; position: relative;');"
		+ "document.getElementsByTagName('body')[0].appendChild(jFunkAnchorTag);"
		+ "}());";

	private static final String JS_REMOVE_OPEN_WINDOW_LINK = "(function() { "
		+ "var jFunkAnchorTag = document.getElementById('%s');"
		+ "jFunkAnchorTag.parentNode.removeChild(jFunkAnchorTag);"
		+ "}());";

	private static final String JS_GET_BOUNDING_CLIENT_RECT = "return arguments[0].getBoundingClientRect();";

	private static final String JS_ELEMENT_FROM_POINT = "return document.elementFromPoint(arguments[0], arguments[1]);";

	private final WebDriver webDriver;
	private final WebElementFinder wef;
	private final FormInputHandler fih;
	private final Map<String, DataSet> dataSets;

	@Inject
	WebDriverTool(final WebDriver webDriver, final WebElementFinder wef, final FormInputHandler fih,
		final Map<String, DataSet> dataSets) {
		this.webDriver = webDriver;
		this.wef = wef;
		this.fih = fih;
		this.dataSets = dataSets;
	}

	public void get(final String url) {
		logger.info("GET {}", url);
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
		WebElement source = findElement(sourceBy);
		WebElement target = findElement(targetBy);
		new Actions(webDriver).dragAndDrop(source, target).perform();
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
	 * Repeatedly applies the current {@link WebDriver} instance to the specifed function until
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
		logger.info("Trying to click on {}", by);
		List<WebElement> elements = wef.timeout(2L).by(by).findAll();
		if (elements.size() > 0) {
			elements.get(0).click();
			logger.info("Click successful");
			return true;
		}
		logger.info("Click not successful");
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
		WebElement element = findElement(by);
		new Actions(webDriver).click(element).perform();
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
		WebElement element = findElement(by);
		Rectangle rect = getBoundingClientRect(by);
		Point p = clickSpecs.getPoint(rect);

		logger.info("Clicking on {} at offset: {}(x={}, y={})", by, clickSpecs, p.x, p.y);
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
		fih.by(by).value(value).perform();
	}

	/**
	 * Returns the size of an element and its position relative to the viewport. Uses JavaScript
	 * calling Element.getBoundingClientRect().
	 *
	 * @param by
	 *            the {@link By} used to locate the element
	 * @returnthe rectangle
	 */
	public Rectangle getBoundingClientRect(final By by) {
		WebElement el = findElement(by);

		@SuppressWarnings("unchecked")
		Map<String, Number> result = (Map<String, Number>) executeScript(JS_GET_BOUNDING_CLIENT_RECT, el);
		Rectangle rectangle = new Rectangle(result.get("top").intValue(), result.get("left").intValue(),
			result.get("bottom").intValue(), result.get("right").intValue(), result.get("width").intValue(),
			result.get("height").intValue());

		logger.info("Bounding client rect for {}: {}", by, rectangle);
		return rectangle;
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
		logger.info("executeScript: {}", new ToStringBuilder(this, LoggingToStringStyle.INSTANCE).append("script", script).append("args", args));
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
		logger.info("executeAsyncScript: {}", new ToStringBuilder(this, LoggingToStringStyle.INSTANCE).append("script", script).append("args", args));
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
		return openNewWindow(() -> click(openClickBy), timeoutSeconds);
	}

	/**
	 * Opens a new window blank window ({@code about:blank}) and switches to it. The new window
	 * is opened by inserting a new link with {@code target='_blank'} and
	 * {@code href='about:blank'} at the end of the page, which is then clicked and removed
	 * again afterwards.
	 *
	 * @return the handle of the window that opened the new window
	 */
	public String openNewWindow() {
		return openNewWindow("about:blank");
	}

	/**
	 * Opens a new window, switches to it, and loads the given URL in the new window. The new
	 * window is opened by inserting a new link with {@code target='_blank'} and
	 * {@code href='about:blank'} at the end of the page, which is then clicked and removed
	 * again afterwards.
	 *
	 * @param url
	 *            the url to open
	 * @return the handle of the window that opened the new window
	 */
	public String openNewWindow(final String url) {
		String id = UUID.randomUUID().toString();
		// add link
		((JavascriptExecutor) webDriver).executeScript(String.format(JS_APPEND_OPEN_WINDOW_LINK, id, url));
		String oldHandle = openNewWindow(By.id(id), 2L);
		String newHandle = webDriver.getWindowHandle();

		// remove link again
		webDriver.switchTo().window(oldHandle);
		((JavascriptExecutor) webDriver).executeScript(String.format(JS_REMOVE_OPEN_WINDOW_LINK, id));

		webDriver.switchTo().window(newHandle);
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
		logger.info("Switching WebDriver...");
		return webDriver.switchTo();
	}

	/**
	 * Issues a log message before executing {@link WebDriver#close()}.
	 */
	public void close() {
		logger.info("Closing window: {}", webDriver.getTitle());
		webDriver.close();
	}
}

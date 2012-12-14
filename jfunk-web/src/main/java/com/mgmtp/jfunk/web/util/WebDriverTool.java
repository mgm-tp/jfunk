/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.util;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.difference;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets.SetView;
import com.mgmtp.jfunk.common.config.ScriptScoped;
import com.mgmtp.jfunk.data.DataSet;

/**
 * @author rnaegele
 * @version $Id: $
 */
@ScriptScoped
public class WebDriverTool {

	private static final String APPEND_OPEN_WINDOW_LINK_SCRIPT_TEMPLATE = "(function() { "
			+ "var jFunkAnchorTag = document.createElement('a');"
			+ "jFunkAnchorTag.appendChild(document.createTextNode('jfunk-new-window-link'));"
			+ "jFunkAnchorTag.setAttribute('id', '%s');"
			+ "jFunkAnchorTag.setAttribute('href', '%s');"
			+ "jFunkAnchorTag.setAttribute('target', '_blank');"
			+ "jFunkAnchorTag.setAttribute('style', 'display:block; z-index: 100000; position: relative;');"
			+ "document.getElementsByTagName('body')[0].appendChild(jFunkAnchorTag);"
			+ "}());";

	private static final String REMOVE_OPEN_WINDOW_LINK_SCRIPT_TEMPLATE = "(function() { "
			+ "var jFunkAnchorTag = document.getElementById('%s');"
			+ "jFunkAnchorTag.parentNode.removeChild(jFunkAnchorTag);"
			+ "}());";

	private final WebDriver webDriver;
	private final WebElementFinder wef;
	private final FormInputHandler fih;
	private final Map<String, DataSet> dataSets;

	@Inject
	WebDriverTool(final WebDriver webDriver, final WebElementFinder wef, final FormInputHandler fih, final Map<String, DataSet> dataSets) {
		this.webDriver = webDriver;
		this.wef = wef;
		this.fih = fih;
		this.dataSets = dataSets;
	}

	public WebElement find(final By by) {
		return wef.by(by).find();
	}

	public WebElement find(final By by, final Predicate<WebElement> condition) {
		return wef.by(by).condition(condition).find();
	}

	public List<WebElement> findAll(final By by) {
		return wef.by(by).findAll();
	}

	public List<WebElement> findAll(final By by, final Predicate<WebElement> condition) {
		return wef.by(by).condition(condition).findAll();
	}

	public void waitUntilNotFound(final By by) {
		for (int i = 0; i < 120; ++i) {
			if (WebElementFinder.create().webDriver(webDriver).by(by).findAll().isEmpty()) {
				return;
			}
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		throw new WebDriverException("Element has not disappeared: " + by);
	}

	public boolean tryClick(final By by) {
		List<WebElement> elements = wef.timeout(2L).by(by).findAll();
		if (elements.size() > 0) {
			elements.get(0).click();
			return true;
		}
		return false;
	}

	public void sendKeys(final By by, final CharSequence... keysToSend) {
		find(by).sendKeys(keysToSend);
	}

	public void clear(final By by) {
		find(by).clear();
	}

	public void click(final By by) {
		find(by).click();
	}

	public void contextClick(final By by) {
		WebElement element = find(by);
		new Actions(webDriver).contextClick(element).perform();
	}

	public void hover(final By by) {
		WebElement element = find(by);
		new Actions(webDriver).moveToElement(element).perform();
	}

	public WebElement hover(final By by, final By byToAppear) {
		WebElement element = find(by);
		new Actions(webDriver).moveToElement(element).perform();

		WebElementFinder finder = wef.timeout(1L, 200L).by(byToAppear);
		RuntimeException exception = null;
		for (int i = 0; i < 10; ++i) {
			try {
				return finder.find();
			} catch (NoSuchElementException ex) {
				exception = ex;
			} catch (TimeoutException ex) {
				exception = ex;
			}
		}
		throw exception;
	}

	public String getAttributeValue(final By by, final String attributeName) {
		WebElement element = find(by);
		return element.getAttribute(attributeName);
	}

	public String getElementText(final By by) {
		return getElementText(by, true);
	}

	public String getElementText(final By by, final boolean normalizeSpace) {
		WebElement element = find(by);
		String text = element.getText();
		return normalizeSpace ? StringUtils.normalizeSpace(text) : text;
	}

	public void processField(final By by, final String dataSetKey, final String dataKey) {
		fih.by(by).dataSet(dataSets.get(dataSetKey)).dataKey(dataKey).perform();
	}

	public void processField(final By by, final String dataSetKey, final String dataKey, final Integer dataIndex) {
		fih.by(by).dataSet(dataSets.get(dataSetKey)).dataKeyWithIndex(dataKey, dataIndex).perform();
	}

	public void processField(final By by, final String value) {
		fih.by(by).value(value).perform();
	}

	/**
	 * Opens a new window and switches to it. The window to switch to is determined by diffing the
	 * given {@code existingWindowHandles} with the current ones. The difference must be exactly one
	 * window handle which is then used to switch to.
	 * 
	 * @param openClickBy
	 *            identifies the element to click on in order to open the new window
	 * @param timeoutSeconds
	 *            the timeout in seconds to wait for the new window to open
	 * @return the handle of window that opened the new window
	 */
	public String openNewWindow(final By openClickBy, final long timeoutSeconds) {
		return openNewWindow(new Runnable() {
			@Override
			public void run() {
				click(openClickBy);
			}
		}, timeoutSeconds);
	}

	/**
	 * Opens a new window blank window ({@code about:blank}) and switches to it. The new window is
	 * opened by inserting a new link with {@code target='_blank'} and {@code href='about:blank'} at
	 * the end of the page, which is then clicked and removed again afterwards.
	 * 
	 * @return the handle of window that opened the new window
	 */
	public String openNewWindow() {
		return openNewWindow("about:blank");
	}

	/**
	 * Opens a new window, switches to it, and loads the given URL in the new window. The new window
	 * is opened by inserting a new link with {@code target='_blank'} and {@code href='about:blank'}
	 * at the end of the page, which is then clicked and removed again afterwards.
	 * 
	 * @param url
	 *            the url to open
	 * @return the handle of window that opened the new window
	 */
	public String openNewWindow(final String url) {
		String id = UUID.randomUUID().toString();
		// add link
		((JavascriptExecutor) webDriver).executeScript(String.format(APPEND_OPEN_WINDOW_LINK_SCRIPT_TEMPLATE, id, url));
		String oldHandle = openNewWindow(By.id(id), 2L);
		String newHandle = webDriver.getWindowHandle();

		// remove link again
		webDriver.switchTo().window(oldHandle);
		((JavascriptExecutor) webDriver).executeScript(String.format(REMOVE_OPEN_WINDOW_LINK_SCRIPT_TEMPLATE, id));

		webDriver.switchTo().window(newHandle);
		return oldHandle;
	}

	/**
	 * Opens a new window and switches to it. The window to switch to is determined by diffing the
	 * given {@code existingWindowHandles} with the current ones. The difference must be exactly one
	 * window handle which is then used to switch to.
	 * 
	 * @param openCommand
	 *            logic for opening the new window
	 * @param timeoutSeconds
	 *            the timeout in seconds to wait for the new window to open
	 * @return the handle of window that opened the new window
	 */
	public String openNewWindow(final Runnable openCommand, final long timeoutSeconds) {
		String oldHandle = webDriver.getWindowHandle();
		final Set<String> existingWindowHandles = webDriver.getWindowHandles();

		openCommand.run();

		BasePredicate<WebDriver, String> predicate = new BasePredicate<WebDriver, String>() {
			private String result;

			@Override
			protected boolean doApply(final WebDriver input) {
				Set<String> newWindowHandles = webDriver.getWindowHandles();
				SetView<String> newWindows = difference(newWindowHandles, existingWindowHandles);
				if (newWindows.isEmpty()) {
					throw new NotFoundException("No new window found.");
				}
				result = getOnlyElement(newWindows);
				return true;
			}

			@Override
			public String getResult() {
				return result;
			}
		};

		WebDriverWait wait = new WebDriverWait(webDriver, timeoutSeconds);
		wait.until(predicate);

		String newHandle = predicate.getResult();
		webDriver.switchTo().window(newHandle);
		return oldHandle;
	}
}

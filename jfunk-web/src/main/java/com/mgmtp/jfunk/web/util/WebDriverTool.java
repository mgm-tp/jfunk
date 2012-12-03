package com.mgmtp.jfunk.web.util;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.difference;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.collect.Sets.SetView;
import com.mgmtp.jfunk.common.config.ScriptScoped;
import com.mgmtp.jfunk.data.DataSet;

/**
 * @author rnaegele
 * @version $Id: $
 */
@ScriptScoped
public class WebDriverTool {

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

	public List<WebElement> findAll(final By by) {
		return wef.by(by).findAll();
	}

	public void waitUntilNotFound(final By by) {
		for (int i = 0; i < 20; ++i) {
			if (WebElementFinder.create().webDriver(webDriver).by(by).findAll().isEmpty()) {
				return;
			}
			try {
				Thread.sleep(100L);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		throw new WebDriverException("Element has not disappeared: " + by);
	}

	public void tryClick(final By by) {
		List<WebElement> elements = wef.timeout(2L).by(by).findAll();
		if (elements.size() > 0) {
			elements.get(0).click();
		}
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
	 * @return the handle of the newly opened window
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
	 * Opens a new window and switches to it. The window to switch to is determined by diffing the
	 * given {@code existingWindowHandles} with the current ones. The difference must be exactly one
	 * window handle which is then used to switch to.
	 * 
	 * @param openCommand
	 *            logic for opening the new window
	 * @param timeoutSeconds
	 *            the timeout in seconds to wait for the new window to open
	 * @return the handle of the newly opened window
	 */
	public String openNewWindow(final Runnable openCommand, final long timeoutSeconds) {
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
		return newHandle;
	}
}

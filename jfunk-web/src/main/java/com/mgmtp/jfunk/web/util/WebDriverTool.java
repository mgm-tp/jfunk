package com.mgmtp.jfunk.web.util;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

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
}

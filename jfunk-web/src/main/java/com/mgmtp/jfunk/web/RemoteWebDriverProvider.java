package com.mgmtp.jfunk.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import com.mgmtp.jfunk.common.util.Configuration;

/**
 * RemoteWebDriverProvider
 * 
 * @date $LastChangedDate$
 * @revision $LastChangedRevision$
 */
public class RemoteWebDriverProvider extends BaseWebDriverProvider {

	@Inject
	public RemoteWebDriverProvider(final Configuration config, final Set<WebDriverEventListener> eventListeners) {
		super(config, eventListeners);
	}

	@Override
	protected WebDriver createWebDriver() {
		String remoteWebDriverUrl = config.get(WebConstants.REMOTE_WEBDRIVER_URL, "");
		Validate.notBlank(remoteWebDriverUrl, "Property '%s' must be set in configuration", WebConstants.REMOTE_WEBDRIVER_URL);

		String remoteWebDriverBrowser = config.get(WebConstants.REMOTE_WEBDRIVER_BROWSER);
		Validate.notBlank(remoteWebDriverBrowser, "Property '%s' must be set in configuration", WebConstants.REMOTE_WEBDRIVER_BROWSER);

		String remoteWebDriverBrowserVersion = config.get(WebConstants.REMOTE_WEBDRIVER_BROWSER_VERSION, "");
		boolean remoteWebDriverJavaScriptEnabled = config.getBoolean(WebConstants.REMOTE_WEBDRIVER_ENABLE_JAVASCRIPT, true);

		URL url;
		try {
			url = new URL(remoteWebDriverUrl);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Illegal remote web driver hub url: " + remoteWebDriverUrl);
		}

		DesiredCapabilities capabilities = new DesiredCapabilities(remoteWebDriverBrowser, remoteWebDriverBrowserVersion, Platform.ANY);
		capabilities.setJavascriptEnabled(remoteWebDriverJavaScriptEnabled);

		log.info("Starting remote web driver with capability: {}", capabilities);
		return new RemoteWebDriver(url, capabilities);
	}
}

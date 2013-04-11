/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
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
	public RemoteWebDriverProvider(final Configuration config, final Set<WebDriverEventListener> eventListeners,
			final Map<String, Capabilities> capabilitiesMap) {
		super(config, eventListeners, capabilitiesMap);
	}

	@Override
	protected WebDriver createWebDriver() {
		String remoteWebDriverUrl = config.get(WebConstants.REMOTE_WEBDRIVER_URL, "");
		Validate.notBlank(remoteWebDriverUrl, "Property '%s' must be set in configuration", WebConstants.REMOTE_WEBDRIVER_URL);

		URL url;
		try {
			url = new URL(remoteWebDriverUrl);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Illegal remote web driver hub url: " + remoteWebDriverUrl);
		}

		log.info("Starting remote web driver with capabilitiesMap: {}", capabilitiesMap);
		return new Augmenter().augment(new RemoteWebDriver(url, capabilitiesMap.get(WebConstants.WEBDRIVER_REMOTE)));
	}
}

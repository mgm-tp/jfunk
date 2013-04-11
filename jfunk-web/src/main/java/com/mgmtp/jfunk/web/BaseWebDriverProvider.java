/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Provider;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgmtp.jfunk.common.util.Configuration;

/**
 * @author rnaegele
 */
public abstract class BaseWebDriverProvider implements Provider<WebDriver> {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected final Configuration config;
	private final Set<WebDriverEventListener> eventListeners;
	protected final Map<String, Capabilities> capabilitiesMap;

	public BaseWebDriverProvider(final Configuration config, final Set<WebDriverEventListener> eventListeners,
			final Map<String, Capabilities> capabilitiesMap) {
		this.config = config;
		this.eventListeners = eventListeners;
		this.capabilitiesMap = capabilitiesMap;
	}

	@Override
	public WebDriver get() {
		log.info("Creating new WebDriver instance with key '{}'...", config.get(WebConstants.WEBDRIVER_KEY));

		WebDriver webDriver = createWebDriver();
		checkState(!(webDriver instanceof EventFiringWebDriver),
				"WebDrivers must not be wrapped explicitly into an EventFiringWebDriver. This is implicitly done by jFunk.");

		long implicitWaitSeconds = config.getLong(WebConstants.WEBDRIVER_IMPLICIT_WAIT_SECONDS, 0L);
		if (implicitWaitSeconds > 0) {
			webDriver.manage().timeouts().implicitlyWait(implicitWaitSeconds, TimeUnit.SECONDS);
		}

		EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(webDriver);
		for (WebDriverEventListener listener : eventListeners) {
			eventFiringWebDriver.register(listener);
		}
		return eventFiringWebDriver;
	}

	protected abstract WebDriver createWebDriver();
}

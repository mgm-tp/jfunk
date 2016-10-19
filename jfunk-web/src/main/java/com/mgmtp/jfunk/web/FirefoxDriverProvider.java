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
package com.mgmtp.jfunk.web;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.WebDriverEventListener;

import com.google.common.eventbus.EventBus;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.config.InjectConfig;

/**
 * @author rnaegele
 */
public class FirefoxDriverProvider extends BaseWebDriverProvider {

	@InjectConfig(name = FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, defaultValue="false")
	private static String driverUseMarionetteScriptProperty;

	@Inject
	protected FirefoxDriverProvider(final Configuration config, final Set<WebDriverEventListener> eventListeners,
			final Map<String, DesiredCapabilities> capabilitiesMap, final EventBus eventBus) {
		super(config, eventListeners, capabilitiesMap, eventBus);
	}

	@Override
	protected WebDriver createWebDriver(final DesiredCapabilities capabilities) {
		//TODO this flag tells FirefoxDriver whether to run in legacy mode or in Marionette mode
		// => remove it, once the W3C actions API is correctly implemented in GeckoDriver
		System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, driverUseMarionetteScriptProperty);
		return new FirefoxDriver(capabilities);
	}
}

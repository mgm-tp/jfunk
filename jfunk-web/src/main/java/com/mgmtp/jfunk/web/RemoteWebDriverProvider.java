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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import com.google.common.eventbus.EventBus;
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
			final Map<String, DesiredCapabilities> capabilitiesMap, final EventBus eventBus) {
		super(config, eventListeners, capabilitiesMap, eventBus);
	}

	@Override
	protected WebDriver createWebDriver(final DesiredCapabilities capabilities) {
		String remoteWebDriverUrl = config.get(WebConstants.REMOTE_WEBDRIVER_URL, "");
		Validate.notBlank(remoteWebDriverUrl, "Property '%s' must be set in configuration", WebConstants.REMOTE_WEBDRIVER_URL);

		URL url;
		try {
			url = new URL(remoteWebDriverUrl);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Illegal remote web driver hub url: " + remoteWebDriverUrl);
		}

		log.info("Starting remote web driver with capabilitiesMap: {}", capabilitiesMap);
		return new Augmenter().augment(new RemoteWebDriver(url, capabilities));
	}
}

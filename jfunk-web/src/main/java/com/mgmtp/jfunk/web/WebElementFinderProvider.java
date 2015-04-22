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

import static org.apache.commons.lang3.StringUtils.trimToNull;

import javax.inject.Inject;
import javax.inject.Provider;

import org.openqa.selenium.WebDriver;

import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.web.util.WebElementFinder;

/**
 * Guice provider for {@link WebElementFinder}.
 * 
 * @author rnaegele
 * @since 3.1
 */
class WebElementFinderProvider implements Provider<WebElementFinder> {

	private final Provider<WebDriver> webDriverProvider;
	private final Provider<Configuration> configProvider;

	@Inject
	WebElementFinderProvider(final Provider<WebDriver> webDriverProvider, final Provider<Configuration> configProvider) {
		this.webDriverProvider = webDriverProvider;
		this.configProvider = configProvider;

	}

	@Override
	public WebElementFinder get() {
		WebElementFinder wef = WebElementFinder.create().webDriver(webDriverProvider.get());

		Configuration config = configProvider.get();
		String value = trimToNull(config.get(WebConstants.WEF_ENABLED));
		if (value != null) {
			wef = wef.enabled(Boolean.parseBoolean(value));
		}
		value = trimToNull(config.get(WebConstants.WEF_DISPLAYED));
		if (value != null) {
			wef = wef.displayed(Boolean.parseBoolean(value));
		}
		long timeout = config.getLong(WebConstants.WEF_TIMEOUT_SECONDS, 0L);
		if (timeout > 0L) {
			long sleepMillis = config.getLong(WebConstants.WEF_SLEEP_MILLIS, 0L);
			wef = sleepMillis > 0L ? wef.timeout(timeout, sleepMillis) : wef.timeout(timeout);
		}

		return wef;
	}
}

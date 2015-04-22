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

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.common.util.Disposable;

/**
 * {@link Disposable} for quitting a {@link WebDriver} instance.
 * 
 * @author rnaegele
 */
@Singleton
public class WebDriverDisposable implements Disposable<WebDriver> {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Provider<Configuration> configurationProvider;

	@Inject
	public WebDriverDisposable(final Provider<Configuration> configurationProvider) {
		this.configurationProvider = configurationProvider;
	}

	/**
	 * Calls {@link WebDriver#quit()} on the instance return by the provider.
	 */
	@Override
	public void dispose(final WebDriver source) {
		if (configurationProvider.get().getBoolean(WebConstants.WEBDRIVER_DONT_QUIT)) {
			return;
		}
		logger.info("Quitting WebDriver...");
		source.quit();
	}
}

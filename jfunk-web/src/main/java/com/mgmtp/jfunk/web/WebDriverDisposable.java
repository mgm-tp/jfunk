/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.openqa.selenium.WebDriver;

import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.common.util.Disposable;

/**
 * {@link Disposable} for quitting a {@link WebDriver} instance.
 * 
 * @author rnaegele
 */
@Singleton
public class WebDriverDisposable implements Disposable {

	private final Provider<WebDriver> webDriverProvider;
	private final Provider<Configuration> configurationProvider;

	@Inject
	public WebDriverDisposable(final Provider<WebDriver> webDriverProvider, final Provider<Configuration> configurationProvider) {
		this.webDriverProvider = webDriverProvider;
		this.configurationProvider = configurationProvider;
	}

	/**
	 * Calls {@link WebDriver#quit()} on the instance return by the provider.
	 */
	@Override
	public void dispose() {
		if (configurationProvider.get().getBoolean(WebConstants.WEBDRIVER_DONT_QUIT)) {
			return;
		}
		webDriverProvider.get().quit();
	}

}

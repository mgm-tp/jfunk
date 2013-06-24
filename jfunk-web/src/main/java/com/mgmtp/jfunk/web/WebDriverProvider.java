/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Provider;

import org.openqa.selenium.WebDriver;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.mgmtp.jfunk.common.util.Configuration;

/**
 * Guice provider that provides an unscoped {@link WebDriver} instance looking up the actual
 * WebDriver by its configuration key in the {@link Injector}.
 * 
 * @author rnaegele
 * @since 3.1
 */
class WebDriverProvider implements Provider<WebDriver> {

	private final Injector injector;
	private final Provider<Configuration> configProvider;

	@Inject
	WebDriverProvider(final Injector injector, final Provider<Configuration> configProvider) {
		this.injector = injector;
		this.configProvider = configProvider;
	}

	@Override
	public WebDriver get() {
		String webDriverKey = configProvider.get().get(WebConstants.WEBDRIVER_KEY);
		checkNotNull(webDriverKey, "Property '%s' is null. Check your configuration.", WebConstants.WEBDRIVER_KEY);
		return injector.getInstance(Key.get(WebDriver.class, Names.named(webDriverKey)));
	}
}
/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web;

import static org.apache.commons.lang3.StringUtils.trimToNull;

import javax.inject.Inject;
import javax.inject.Provider;

import org.openqa.selenium.WebDriver;

import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.web.util.FormInputHandler;

/**
 * Guice provider for {@link FormInputHandler}.
 * 
 * @author rnaegele
 * @since 3.1
 */
class FormInputHandlerProvider implements Provider<FormInputHandler> {

	private final Provider<WebDriver> webDriverProvider;
	private final Provider<Configuration> configProvider;

	@Inject
	FormInputHandlerProvider(final Provider<WebDriver> webDriverProvider, final Provider<Configuration> configProvider) {
		this.webDriverProvider = webDriverProvider;
		this.configProvider = configProvider;

	}

	@Override
	public FormInputHandler get() {
		FormInputHandler fih = FormInputHandler.create().webDriver(webDriverProvider.get());

		Configuration config = configProvider.get();
		String value = trimToNull(config.get(WebConstants.FIH_ENABLED));
		if (value != null) {
			fih = fih.enabled(Boolean.parseBoolean(value));
		}
		value = trimToNull(config.get(WebConstants.FIH_DISPLAYED));
		if (value != null) {
			fih = fih.displayed(Boolean.parseBoolean(value));
		}
		value = trimToNull(config.get(WebConstants.FIH_SELECTED));
		if (value != null) {
			fih = fih.selected(Boolean.parseBoolean(value));
		}
		long timeout = config.getLong(WebConstants.FIH_TIMEOUT_SECONDS, 0L);
		if (timeout > 0L) {
			long sleepMillis = config.getLong(WebConstants.FIH_SLEEP_MILLIS, 0L);
			fih = sleepMillis > 0L ? fih.timeout(timeout, sleepMillis) : fih.timeout(timeout);
		}

		return fih;
	}
}

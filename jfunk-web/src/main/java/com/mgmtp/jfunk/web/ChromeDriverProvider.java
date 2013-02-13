/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web;

import java.util.Set;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import com.mgmtp.jfunk.common.util.Configuration;

/**
 * @author rnaegele
 * @version $Id$
 */
public class ChromeDriverProvider extends BaseWebDriverProvider {

	@Inject
	protected ChromeDriverProvider(final Configuration config, final Set<WebDriverEventListener> eventListeners) {
		super(config, eventListeners);
	}

	@Override
	protected WebDriver createWebDriver() {
		return new ChromeDriver();
	}
}

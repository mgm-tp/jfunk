/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.step;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;

import com.mgmtp.jfunk.core.step.base.BaseStep;

/**
 * @author rnaegele
 * @version $Id$
 */
public abstract class WebDriverStep extends BaseStep {

	private WebDriver webDriver;

	public WebDriverStep() {
		super();
	}

	public WebDriverStep(final String name) {
		super(name);
	}

	/**
	 * @return the webDriver
	 */
	public WebDriver getWebDriver() {
		return webDriver;
	}

	/**
	 * @param webDriver
	 *            the webDriver to set
	 */
	@Inject
	public void setWebDriver(final WebDriver webDriver) {
		this.webDriver = webDriver;
	}
}

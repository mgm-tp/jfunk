/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.step;

import org.openqa.selenium.By;

/**
 * @author rnaegele
 */
public class SendKeysStep extends WebDriverStep {

	private final By by;
	private final CharSequence keysToSend;

	public SendKeysStep(final By by, final CharSequence keysToSend) {
		this.by = by;
		this.keysToSend = keysToSend;
	}

	@Override
	public void execute() {
		getWebDriver().findElement(by).sendKeys(keysToSend);
	}
}

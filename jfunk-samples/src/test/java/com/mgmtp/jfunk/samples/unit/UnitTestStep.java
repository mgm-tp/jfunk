/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.samples.unit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.mgmtp.jfunk.web.step.CheckHtml4Pattern;
import com.mgmtp.jfunk.web.step.ComplexWebDriverStep;

/**
 * @author rnaegele
 */
public class UnitTestStep extends ComplexWebDriverStep {

	private final String searchTerm;

	UnitTestStep(final String searchTerm) {
		super("google", "google");
		this.searchTerm = searchTerm;
	}

	@Override
	protected void executeSteps() {
		webDriver.get("http://www.google.com");

		wdt.processField(By.name("q"), searchTerm);
		wdt.sendKeys(By.name("q"), Keys.ENTER);

		executeStep(new CheckHtml4Pattern(String.format("(?s).*%s.*", searchTerm)));
	}
}
/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.step;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author rnaegele
 * @version $Id$
 */
public class CheckHtml4StringTest {

	private WebDriver webDriver;

	@BeforeTest
	public void setUpWebDriver() {
		webDriver = mock(WebDriver.class);
		when(webDriver.getPageSource()).thenReturn("Some Case-Sensitive Test String");
	}

	@Test
	public void stringMustExistCaseSensitively() {
		CheckHtml4String checkHtml4String = new CheckHtml4String("Case-Sensitive");
		checkHtml4String.setWebDriver(webDriver);
		checkHtml4String.execute();
	}

	@Test
	public void stringMustExistNotCaseSensitively() {
		CheckHtml4String checkHtml4String = new CheckHtml4String("case-sensitive", false);
		checkHtml4String.setWebDriver(webDriver);
		checkHtml4String.execute();
	}

	@Test
	public void stringMustNotExistCaseSensitively() {
		CheckHtml4String checkHtml4String = new CheckHtml4String("foo", true, false);
		checkHtml4String.setWebDriver(webDriver);
		checkHtml4String.execute();
	}

	@Test
	public void stringMustNotExistNotCaseSensitively() {
		CheckHtml4String checkHtml4String = new CheckHtml4String("foo", false, false);
		checkHtml4String.setWebDriver(webDriver);
		checkHtml4String.execute();
	}
}

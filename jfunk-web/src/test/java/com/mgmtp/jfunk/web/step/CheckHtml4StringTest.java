/*
 * Copyright (c) 2013 mgm technology partners GmbH
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
package com.mgmtp.jfunk.web.step;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author rnaegele
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

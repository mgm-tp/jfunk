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

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.mgmtp.jfunk.core.exception.StepException;

/**
 * @author rnaegele
 */
public class Check4ElementTest {

	private void doTest(final boolean elementFound, final boolean mustExist) {
		By by = By.id("foo");
		WebDriver webDriver = mock(WebDriver.class);
		if (elementFound) {
			WebElement webElement = mock(WebElement.class);
			when(webElement.isEnabled()).thenReturn(true);
			when(webElement.isDisplayed()).thenReturn(true);
			when(webDriver.findElements(by)).thenReturn(asList(webElement));
		} else {
			when(webDriver.findElements(by)).thenReturn(ImmutableList.<WebElement>of());
		}
		Check4Element step = new Check4Element(by, mustExist);
		step.setWebDriver(webDriver);
		step.execute();
	}

	@Test
	public void testElementFoundAndMustExist() {
		doTest(true, true);
	}

	@Test(expectedExceptions = StepException.class)
	public void testElementFoundAndMustNotExist() {
		doTest(true, false);
	}

	@Test(expectedExceptions = StepException.class)
	public void testElementNotFoundAndMustExist() {
		doTest(false, true);
	}

	@Test
	public void testElementNotFoundAndMustNotExist() {
		doTest(false, false);
	}
}

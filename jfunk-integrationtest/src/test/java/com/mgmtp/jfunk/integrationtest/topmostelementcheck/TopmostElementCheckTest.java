/*
 * Copyright (c) 2016 mgm technology partners GmbH
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
package com.mgmtp.jfunk.integrationtest.topmostelementcheck;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.unit.JFunkJUnitSupport;
import com.mgmtp.jfunk.unit.JFunkRunner;
import com.mgmtp.jfunk.web.WebConstants;

/**
 * @author sstrohmaier
 */
@RunWith(JFunkJUnitSupport.class)
public class TopmostElementCheckTest {

	@Inject
	JFunkRunner jFunkRunner;

	// Button Action Tests

	@Test
	public void testInternetExplorerTopmostElementCheckTrueButtonAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_INTERNET_EXPLORER, JFunkConstants.TRUE, true, Subtest.BUTTON_ACTION);
	}

	@Test
	public void testInternetExplorerTopmostElementCheckFalseButtonAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_INTERNET_EXPLORER, JFunkConstants.FALSE, false, Subtest.BUTTON_ACTION);
	}

	@Test
	public void testChromeTopmostElementCheckTrueButtonAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_CHROME, JFunkConstants.TRUE, true, Subtest.BUTTON_ACTION);
	}

	@Test
	public void testChromeTopmostElementCheckFalseButtonAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_CHROME, JFunkConstants.FALSE, false, Subtest.BUTTON_ACTION);
	}

	@Test
	public void testFirefoxTopmostElementCheckTrueButtonAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_FIREFOX, JFunkConstants.TRUE, true, Subtest.BUTTON_ACTION);
	}

	@Test
	public void testFirefoxTopmostElementCheckFalseButtonAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_FIREFOX, JFunkConstants.FALSE, false, Subtest.BUTTON_ACTION);
	}

	// Input Action Tests

	@Test
	public void testInternetExplorerTopmostElementCheckTrueInputAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_INTERNET_EXPLORER, JFunkConstants.TRUE, true, Subtest.INPUT_ACTION);
	}

	@Test
	public void testInternetExplorerTopmostElementCheckFalseInputAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_INTERNET_EXPLORER, JFunkConstants.FALSE, false, Subtest.INPUT_ACTION);
	}

	@Test
	public void testChromeTopmostElementCheckTrueInputAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_CHROME, JFunkConstants.TRUE, true, Subtest.INPUT_ACTION);
	}

	@Test
	public void testChromeTopmostElementCheckFalseInputAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_CHROME, JFunkConstants.FALSE, false, Subtest.INPUT_ACTION);
	}

	@Test
	public void testFirefoxTopmostElementCheckTrueInputAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_FIREFOX, JFunkConstants.TRUE, true, Subtest.INPUT_ACTION);
	}

	@Test
	public void testFirefoxTopmostElementCheckFalseInputAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_FIREFOX, JFunkConstants.FALSE, false, Subtest.INPUT_ACTION);
	}

	// Drag & Drop Action Tests

	@Test
	public void testInternetExplorerTopmostElementCheckTrueDragAndDropAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_INTERNET_EXPLORER, JFunkConstants.TRUE, true, Subtest.DRAG_AND_DROP_ACTION);
	}

	@Test
	public void testInternetExplorerTopmostElementCheckFalseDragAndDropAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_INTERNET_EXPLORER, JFunkConstants.FALSE, false,
				Subtest.DRAG_AND_DROP_ACTION);
	}

	@Test
	public void testChromeTopmostElementCheckTrueDragAndDropAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_CHROME, JFunkConstants.TRUE, true, Subtest.DRAG_AND_DROP_ACTION);
	}

	@Test
	public void testChromeTopmostElementCheckFalseDragAndDropAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_CHROME, JFunkConstants.FALSE, false, Subtest.DRAG_AND_DROP_ACTION);
	}

	@Test
	public void testFirefoxTopmostElementCheckTrueDragAndDropAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_FIREFOX, JFunkConstants.TRUE, true, Subtest.DRAG_AND_DROP_ACTION);
	}

	@Test
	public void testFirefoxTopmostElementCheckFalseDragAndDropAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_FIREFOX, JFunkConstants.FALSE, false, Subtest.DRAG_AND_DROP_ACTION);
	}

	// Open New Window Action Tests

	@Test
	public void testInternetExplorerTopmostElementCheckTrueNewWindowAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_INTERNET_EXPLORER, JFunkConstants.TRUE, true,
				Subtest.OPEN_NEW_WINDOW_ACTION);
	}

	@Test
	public void testInternetExplorerTopmostElementCheckFalseNewWindowAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_INTERNET_EXPLORER, JFunkConstants.FALSE, false,
				Subtest.OPEN_NEW_WINDOW_ACTION);
	}

	@Test
	public void testChromeTopmostElementCheckTrueOpenNewWindowAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_CHROME, JFunkConstants.TRUE, true, Subtest.OPEN_NEW_WINDOW_ACTION);
	}

	@Test
	public void testChromeTopmostElementCheckFalseOpenNewWindowAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_CHROME, JFunkConstants.FALSE, false, Subtest.OPEN_NEW_WINDOW_ACTION);
	}

	// TODO: this test will fail, as Firefox does not create a new WindowsHandle when opening a new window.
	// Running the test results in a TimeoutException ("Timed out after X seconds waiting for new window to open.")
	// which is caused by a NotFoundException ("No new window found.").
	@Test
	public void testFirefoxTopmostElementCheckTrueOpenNewWindowAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_FIREFOX, JFunkConstants.TRUE, true, Subtest.OPEN_NEW_WINDOW_ACTION);
	}

	// TODO: this test will fail, as Firefox does not create a new WindowsHandle when opening a new window.
	// Running the test results in a TimeoutException ("Timed out after X seconds waiting for new window to open.")
	// which is caused by a NotFoundException ("No new window found.").
	@Test
	public void testFirefoxTopmostElementCheckFalseOpenNewWindowAction() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_FIREFOX, JFunkConstants.FALSE, false, Subtest.OPEN_NEW_WINDOW_ACTION);
	}

	// Corner Cases Tests

	@Test
	public void testInternetExplorerTopmostElementCheckTrueCornerCases() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_INTERNET_EXPLORER, JFunkConstants.TRUE, true,
				Subtest.CORNER_CASES);
	}

	@Test
	public void testInternetExplorerTopmostElementCheckFalseCornerCases() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_INTERNET_EXPLORER, JFunkConstants.FALSE, false,
				Subtest.CORNER_CASES);
	}

	@Test
	public void testChromeTopmostElementCheckTrueCornerCases() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_CHROME, JFunkConstants.TRUE, true, Subtest.CORNER_CASES);
	}

	@Test
	public void testChromeTopmostElementCheckFalseCornerCases() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_CHROME, JFunkConstants.FALSE, false, Subtest.CORNER_CASES);
	}

	@Test
	public void testFirefoxTopmostElementCheckTrueCornerCases() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_FIREFOX, JFunkConstants.TRUE, true, Subtest.CORNER_CASES);
	}

	@Test
	public void testFirefoxTopmostElementCheckFalseCornerCases() {
		testTopmostElementCheck(WebConstants.WEBDRIVER_FIREFOX, JFunkConstants.FALSE, false, Subtest.CORNER_CASES);
	}


	private void testTopmostElementCheck(String webDriverImplementationToTest,
			String valueToSetInConfigForFlagTopmostElementCheck, boolean expectedValueForFlagTopmostElementCheck, Subtest subtest) {
		jFunkRunner.set(JFunkConstants.ARCHIVE_DIR, String.format("testruns/%s/%s/%s",
				AbstractTopmostElementCheckModule.TOPMOST_ELEMENT_CHECK_APPLICATION, webDriverImplementationToTest,
				valueToSetInConfigForFlagTopmostElementCheck));

		jFunkRunner.set(WebConstants.WEBDRIVER_KEY, webDriverImplementationToTest);
		jFunkRunner.set(WebConstants.WDT_TOPMOST_ELEMENT_CHECK, valueToSetInConfigForFlagTopmostElementCheck);

		AbstractTopmostElementCheckModule module = null;
		switch (subtest) {
			case BUTTON_ACTION: {
				module = new TopmostElementCheckButtonActionModule(expectedValueForFlagTopmostElementCheck);
				break;
			}
			case INPUT_ACTION: {
				module = new TopmostElementCheckInputActionModule(expectedValueForFlagTopmostElementCheck);
				break;
			}
			case DRAG_AND_DROP_ACTION: {
				module = new TopmostElementCheckDragAndDropActionModule(expectedValueForFlagTopmostElementCheck);
				break;
			}
			case OPEN_NEW_WINDOW_ACTION: {
				module = new TopmostElementCheckOpenNewWindowActionModule(expectedValueForFlagTopmostElementCheck);
				break;
			}
			case CORNER_CASES: {
				module = new TopmostElementCheckCornerCasesModule(expectedValueForFlagTopmostElementCheck);
				break;
			}
			default: {
				throw new UnsupportedOperationException(String.format("Subtest type %s not supported", subtest));
			}
		}
		jFunkRunner.run(module);
	}

	private enum Subtest {
		BUTTON_ACTION, INPUT_ACTION, DRAG_AND_DROP_ACTION, OPEN_NEW_WINDOW_ACTION, CORNER_CASES
	}

}

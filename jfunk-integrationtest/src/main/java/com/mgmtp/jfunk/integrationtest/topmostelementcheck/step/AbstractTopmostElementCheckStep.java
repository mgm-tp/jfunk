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
package com.mgmtp.jfunk.integrationtest.topmostelementcheck.step;

import org.openqa.selenium.By;

import com.mgmtp.jfunk.integrationtest.AbstractAppServerModule;
import com.mgmtp.jfunk.integrationtest.topmostelementcheck.AbstractTopmostElementCheckModule;
import com.mgmtp.jfunk.web.step.ComplexWebDriverStep;

/**
 * @author sstrohmaier
 */
public abstract class AbstractTopmostElementCheckStep extends ComplexWebDriverStep {

	protected final boolean expectedValueForFlagTopmostElementCheck;

	public AbstractTopmostElementCheckStep(boolean expectedValueForFlagTopmostElementCheck) {
		super();
		this.expectedValueForFlagTopmostElementCheck = expectedValueForFlagTopmostElementCheck;
	}

	@Override
	protected final void executeSteps() {
		wdt.get(AbstractAppServerModule.GRIZZLY_SERVER_BASE_URL + AbstractTopmostElementCheckModule.TOPMOST_ELEMENT_CHECK_APPLICATION);
		executeInnerSteps();

	}

	protected abstract void executeInnerSteps();

	protected void checkWdtActionOnTopmostAndCoveredElement(WdtAction wdtAction, String topmostElementSource,
			String topmostElementTarget, String coveredElementSource, String coveredElementTarget, String inputText) {
		// this WDT action on the topmost element should always be performable
		performWdtAction(wdtAction, topmostElementSource, topmostElementTarget, inputText);
		// the WDT action on the covered element should only be performable if the topMostElementCheck is disabled
		verifyTopMostElementCheckForCoveredElement(wdtAction, coveredElementSource, coveredElementTarget, inputText);
	}

	protected void verifyIsTopmostElement(String webElementId, Boolean expectedIsTopmostElement) {
		By by = By.id(webElementId);
		Boolean actualIsTopmostElement = wdt.isTopmostElement(by);
		if (expectedIsTopmostElement != actualIsTopmostElement) {
			throw new AssertionError(String.format(
					"For WebElement '%s' the 'isTopmostElement' method's actual result '%s' is not the expected result '%s'",
							webElementId, actualIsTopmostElement, expectedIsTopmostElement));
		}
	}

	protected void assertNotTopMostElement(String webElementId) {
		if (!coveredByAssertionErrorOccursWhenPerformingActionOnElement(WdtAction.assertTopmostElement, webElementId, null, null)) {
			throw new WebDriverToolActionException(WdtAction.assertTopmostElement, webElementId, null);
		}
	}

	protected void performWdtAction(WdtAction wdtAction, String webElementIdSource, String webElementIdTarget,
			String inputText) {
		By bySource = By.id(webElementIdSource);
		By byTarget = (webElementIdTarget != null) ? By.id(webElementIdTarget) : null;
		switch (wdtAction) {
			case clear: {
				wdt.clear(bySource);
				break;
			}
			case tryClick: {
				wdt.tryClick(bySource);
				break;
			}
			case click: {
				wdt.click(bySource);
				break;
			}
			case contextClick: {
				wdt.contextClick(bySource);
				// IE: after performing the contextClick, we must click some where else to be able to continue
				wdt.click(By.id("somewhereelse"));
				break;
			}
			case doubleClick: {
				wdt.doubleClick(bySource);
				break;
			}
			case hover: {
				wdt.hover(bySource);
				break;
			}
			case openNewWindow: {
				wdt.openNewWindow(bySource, 3);
				break;
			}
			case processField: {
				wdt.processField(bySource, inputText);
				break;
			}
			case sendKeys: {
				wdt.sendKeys(bySource, inputText);
				break;
			}
			case assertTopmostElement: {
				wdt.assertTopmostElement(bySource);
				break;
			}
			case dragAndDrop: {
				wdt.dragAndDrop(bySource, byTarget);
				break;
			}
			default: {
				throw new UnsupportedOperationException(String.format("The WebDriverToolAction %s is not supported", wdtAction));
			}
		}
		log.info(String.format("WebDriverToolAction '%s' was successfully performed on WebElements (source: %s, target: %s).",
				wdtAction.name(), webElementIdSource, webElementIdTarget));
	}

	protected void verifyTopMostElementCheckForCoveredElement(WdtAction wdtAction, String webElementSource,
			String webElementTarget, String inputText) {
		// try to perform web driver tool action on covered web element
		boolean assertionErrorOccursWhenPerformingActionOnCoveredElement = coveredByAssertionErrorOccursWhenPerformingActionOnElement(
				wdtAction, webElementSource, webElementTarget, inputText);

		// check: given the topMostElementCheck configuration, was this the right behavior?
		if (expectedValueForFlagTopmostElementCheck) {
			if (!assertionErrorOccursWhenPerformingActionOnCoveredElement) {
				throw new AssertionError(
						String.format(
								"WebDriverToolAction '%s' could be performed on WebElements (source: %s, target: %s) "
										+ "although it is / they are covered by (an)other element(s) and the topmost element check is enabled.",
								wdtAction.name(), webElementSource, webElementTarget));
			}
		} else if (!expectedValueForFlagTopmostElementCheck)
			if (assertionErrorOccursWhenPerformingActionOnCoveredElement) {
				throw new AssertionError(String.format(
						"WebDriverToolAction '%s' could not be performed on covered WebElements (source: %s, target: %s) "
								+ "although the topmost element check is disabled.", wdtAction.name(), webElementSource,
						webElementTarget));
			}
	}

	protected boolean coveredByAssertionErrorOccursWhenPerformingActionOnElement(WdtAction wdtAction, String webElementIdSource,
			String webElementIdTarget, String inputText) {
		boolean result = false;
		try {
			performWdtAction(wdtAction, webElementIdSource, webElementIdTarget, inputText);
		} catch (AssertionError assertionError) {
			if (assertionError.getMessage().contains("is covered by")) {
				log.info("The following WebDriverException occurred: " + assertionError.getMessage());
				result = true;
			} else {
				throw new WebDriverToolActionException(wdtAction, webElementIdSource, webElementIdTarget);
			}
		}
		return result;
	}

	protected enum WdtAction {
		// single web element actions
		click, contextClick, doubleClick, hover, tryClick, assertTopmostElement, processField, clear, sendKeys, openNewWindow,
		// dual web element actions
		dragAndDrop
	}

	private class WebDriverToolActionException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public WebDriverToolActionException(WdtAction wdtAction, String webElementIdSource, String webElementIdTarget) {
			super(String.format("An undefined exception occurred when WebDriverToolAction '%s' "
						+ "was performed on WebElements (source: %s, target: %s).", wdtAction.name(), webElementIdSource,
						webElementIdTarget));
		}

	}

}

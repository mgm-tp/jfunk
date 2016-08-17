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

/**
 * @author sstrohmaier
 */
public class TopmostElementCheckInputActionStep extends AbstractTopmostElementCheckStep {

	public TopmostElementCheckInputActionStep(boolean expectedValueForFlagTopmostElementCheck) {
		super(expectedValueForFlagTopmostElementCheck);
	}

	@Override
	protected void executeInnerSteps() {

		String topMostTextInput = "input1";
		String coveredTextInput = "input2";
		String inputText = "bla";

		verifyIsTopmostElement(topMostTextInput, true);
		verifyIsTopmostElement(coveredTextInput, false);

		wdt.assertTopmostElement(By.id(topMostTextInput));
		assertNotTopMostElement(coveredTextInput);

		checkWdtActionOnTopmostAndCoveredElement(WdtAction.processField, topMostTextInput, null, coveredTextInput, null, inputText);
		checkWdtActionOnTopmostAndCoveredElement(WdtAction.clear, topMostTextInput, null, coveredTextInput, null, null);
		checkWdtActionOnTopmostAndCoveredElement(WdtAction.sendKeys, topMostTextInput, null, coveredTextInput, null, inputText);

	}

}

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
public class TopmostElementCheckButtonActionStep extends AbstractTopmostElementCheckStep {

	public TopmostElementCheckButtonActionStep(boolean expectedValueForFlagTopmostElementCheck) {
		super(expectedValueForFlagTopmostElementCheck);
	}

	@Override
	protected void executeInnerSteps() {

		String topMostButton = "button1";
		String coveredButton = "button2";

		verifyIsTopmostElement(topMostButton, true);
		verifyIsTopmostElement(coveredButton, false);

		wdt.assertTopmostElement(By.id(topMostButton));
		assertNotTopMostElement(coveredButton);

		checkWdtActionOnTopmostAndCoveredElement(WdtAction.tryClick, topMostButton, null, coveredButton, null, null);
		checkWdtActionOnTopmostAndCoveredElement(WdtAction.click, topMostButton, null, coveredButton, null, null);
		checkWdtActionOnTopmostAndCoveredElement(WdtAction.contextClick, topMostButton, null, coveredButton, null, null);
		checkWdtActionOnTopmostAndCoveredElement(WdtAction.doubleClick, topMostButton, null, coveredButton, null, null);
		checkWdtActionOnTopmostAndCoveredElement(WdtAction.hover, topMostButton, null, coveredButton, null, null);

	}

}

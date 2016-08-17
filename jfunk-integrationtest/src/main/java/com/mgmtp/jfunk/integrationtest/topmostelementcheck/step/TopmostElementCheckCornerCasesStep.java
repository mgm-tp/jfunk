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
import org.openqa.selenium.Keys;

/**
 * @author sstrohmaier
 */
public class TopmostElementCheckCornerCasesStep extends AbstractTopmostElementCheckStep {

	public TopmostElementCheckCornerCasesStep(boolean expectedValueForFlagTopmostElementCheck) {
		super(expectedValueForFlagTopmostElementCheck);
	}

	@Override
	protected void executeInnerSteps() {

		// test case: a 'button' is partially overlapped by another

		String topMostButton = "button3";
		String partiallyCoveredButton = "button4";

		verifyIsTopmostElement(topMostButton, true);
		verifyIsTopmostElement(partiallyCoveredButton, false);

		wdt.assertTopmostElement(By.id(topMostButton));
		assertNotTopMostElement(partiallyCoveredButton);

		checkWdtActionOnTopmostAndCoveredElement(WdtAction.click, topMostButton, null, partiallyCoveredButton, null, null);


		// test case: a 'div' contains a single child button as a leaf node

		String divWithSingleChildButton = "div3";
		String singleTopMostLeafNodeButton = "button5";

		verifyIsTopmostElement(singleTopMostLeafNodeButton, true);
		// the topmostElement status cannot be retrieved as the element is not a leaf:
		verifyIsTopmostElement(divWithSingleChildButton, null);

		wdt.assertTopmostElement(By.id(singleTopMostLeafNodeButton));
		// with topmostElementCheck=true, the action is successful but a warning is logged:
		wdt.assertTopmostElement(By.id(divWithSingleChildButton));

		wdt.click(By.id(singleTopMostLeafNodeButton));
		// with topmostElementCheck=true, the action is successful but a warning is logged:
		wdt.click(By.id(divWithSingleChildButton));


		// test case: a visible and clickable 'table' contains two buttons as leaf nodes

		String tableWithTwoButtons = "table1";
		String firstButtonInTable = "button6";
		String secondButtonInTable = "button7";

		verifyIsTopmostElement(firstButtonInTable, true);
		verifyIsTopmostElement(secondButtonInTable, true);
		// the topmostElement status cannot be retrieved as the element is not a leaf:
		verifyIsTopmostElement(tableWithTwoButtons, null);

		wdt.assertTopmostElement(By.id(firstButtonInTable));
		wdt.assertTopmostElement(By.id(secondButtonInTable));
		// with topmostElementCheck=true, the action is successful but a warning is logged:
		wdt.assertTopmostElement(By.id(tableWithTwoButtons));

		wdt.click(By.id(firstButtonInTable));
		wdt.click(By.id(secondButtonInTable));
		// with topmostElementCheck=true, the action is successful but a warning is logged:
		wdt.click(By.id(tableWithTwoButtons));


		// test case: a visible and clickable 'table' contains a single button as a leaf node

		String tableWithOneButtons = "table2";
		String singleButtonInTable = "button8";

		verifyIsTopmostElement(singleButtonInTable, true);
		// the topmostElement status cannot be retrieved as the element is not a leaf:
		verifyIsTopmostElement(tableWithOneButtons, null);

		wdt.assertTopmostElement(By.id(singleButtonInTable));
		// with topmostElementCheck=true, the action is successful but a warning is logged:
		wdt.assertTopmostElement(By.id(tableWithOneButtons));

		wdt.click(By.id(singleButtonInTable));
		// with topmostElementCheck=true, the action is successful but a warning is logged:
		wdt.click(By.id(tableWithOneButtons));


		// test case: a clickable 'link' contains two buttons as leaf nodes

		String linkWithTwoButtons = "link3";
		String firstButtonInLink = "button9";
		String secondButtonInLink = "button10";

		verifyIsTopmostElement(firstButtonInLink, true);
		verifyIsTopmostElement(secondButtonInLink, true);
		// the topmostElement status cannot be retrieved as the element is not a leaf:
		verifyIsTopmostElement(linkWithTwoButtons, null);

		wdt.assertTopmostElement(By.id(firstButtonInLink));
		wdt.assertTopmostElement(By.id(secondButtonInLink));
		// with topmostElementCheck=true, the action is successful but a warning is logged:
		wdt.assertTopmostElement(By.id(linkWithTwoButtons));

		wdt.click(By.id(firstButtonInLink));
		wdt.click(By.id(secondButtonInLink));
		// with topmostElementCheck=true, the action is successful but a warning is logged:
		wdt.click(By.id(linkWithTwoButtons));


		// test case: a clickable 'link' contains a single button as a leaf node

		String linkWithOneButton = "link4";
		String singleButtonInLink = "button11";

		verifyIsTopmostElement(singleButtonInLink, true);
		// the topmostElement status cannot be retrieved as the element is not a leaf:
		verifyIsTopmostElement(linkWithOneButton, null);

		wdt.assertTopmostElement(By.id(singleButtonInLink));
		// with topmostElementCheck=true, the action is successful but a warning is logged:
		wdt.assertTopmostElement(By.id(linkWithOneButton));

		wdt.click(By.id(singleButtonInLink));
		// with topmostElementCheck=true, the action is successful but a warning is logged:
		wdt.click(By.id(linkWithOneButton));


		// test case: WDT actions with the 'body' element

		verifyIsTopmostElement("body1", null);

		// with topmostElementCheck=true, the action is successful but a warning is logged:
		wdt.sendKeys(By.cssSelector("body"), Keys.chord(Keys.CONTROL, "t"));

	}

}

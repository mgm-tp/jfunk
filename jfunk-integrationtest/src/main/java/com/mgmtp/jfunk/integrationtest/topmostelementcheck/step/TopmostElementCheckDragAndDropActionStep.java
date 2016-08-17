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
public class TopmostElementCheckDragAndDropActionStep extends AbstractTopmostElementCheckStep {

	public TopmostElementCheckDragAndDropActionStep(boolean expectedValueForFlagTopmostElementCheck) {
		super(expectedValueForFlagTopmostElementCheck);
	}

	@Override
	protected void executeInnerSteps() {

		String topMostSource = "draggable1";
		String topMostTarget = "droppable1";
		String coveredSource = "draggable2";
		String coveredTarget = "droppable2";

		verifyIsTopmostElement(topMostSource, true);
		verifyIsTopmostElement(topMostTarget, true);
		verifyIsTopmostElement(coveredSource, false);
		verifyIsTopmostElement(coveredTarget, false);

		wdt.assertTopmostElement(By.id(topMostSource));
		wdt.assertTopmostElement(By.id(topMostTarget));
		assertNotTopMostElement(coveredSource);
		assertNotTopMostElement(coveredTarget);

		checkWdtActionOnTopmostAndCoveredElement(WdtAction.dragAndDrop, topMostSource, topMostTarget, coveredSource, coveredTarget,  null);

	}

}

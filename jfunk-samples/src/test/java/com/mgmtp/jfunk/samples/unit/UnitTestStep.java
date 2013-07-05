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
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
package com.mgmtp.jfunk.samples.google;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.mgmtp.jfunk.core.module.TestModuleImpl;
import com.mgmtp.jfunk.core.step.base.StepMode;
import com.mgmtp.jfunk.web.step.CheckHtml4Pattern;
import com.mgmtp.jfunk.web.step.JFunkWebElement;
import com.mgmtp.jfunk.web.step.LoadPage;
import com.mgmtp.jfunk.web.step.SendKeysStep;

public class GoogleModule extends TestModuleImpl {

	public GoogleModule() {
		super("google");
	}

	@Override
	protected void executeSteps() {
		executeSteps(
				new LoadPage("http://www.google.com"),
				new JFunkWebElement(By.name("q"), "searchTerm", getDataSetKey(), StepMode.SET_VALUE),
				new SendKeysStep(By.name("q"), Keys.ENTER),
				new CheckHtml4Pattern("(?s).*" + getDataSet().getValue("searchTerm") + ".*"));
	}
}

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
package com.mgmtp.jfunk.samples.google.page;

import java.util.Map;

import javax.inject.Inject;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.web.page.BasePage;
import com.mgmtp.jfunk.web.util.WebDriverTool;

/**
 * @author rnaegele
 * @since 3.1.0
 */
public class SearchPage extends BasePage {

	private static final By BY_SEARCH_INPUT = By.name("q");
	private static final By BY_SETTINGS_MENU = By.id("fsettl");
	private static final By BY_ADVANCED_SEARCH_MENU = By.cssSelector("#advsl > a");

	@Inject
	public SearchPage(final Map<String, DataSet> dataSets, final WebDriverTool wdt) {
		super("google", dataSets, wdt);
	}

	public void navigateToAdvancedSearchPage() {
		wdt.click(BY_SETTINGS_MENU);
		wdt.click(BY_ADVANCED_SEARCH_MENU);
	}

	public void enterSearchTerm() {
		wdt.processField(BY_SEARCH_INPUT, "google", "searchTerm");
	}

	public void submitPage() {
		wdt.sendKeys(BY_SEARCH_INPUT, Keys.ENTER);
	}
}

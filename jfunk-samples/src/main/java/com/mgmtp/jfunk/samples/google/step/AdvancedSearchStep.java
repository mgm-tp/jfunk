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
package com.mgmtp.jfunk.samples.google.step;

import static com.google.common.base.Preconditions.checkState;

import org.openqa.selenium.By;

import com.mgmtp.jfunk.core.config.InjectConfig;
import com.mgmtp.jfunk.web.step.ComplexWebDriverStep;

/**
 * @author rnaegele
 */
public class AdvancedSearchStep extends ComplexWebDriverStep {

	private static final By BY_SETTINGS_MENU = By.id("fsettl");
	private static final By BY_ADVANCED_SEARCH_MENU = By.cssSelector("#advsl > a");
	private static final By BY_SEARCH_INPUT = By.name("as_q");
	private static final By BY_LANGUAGE_DROPDOWN = By.id("lr_button");
	private static final By BY_COUNTRY_DROPDOWN = By.id("cr_button");
	private static final By BY_SUBMIT_BUTTON = By.xpath("//form[@name='f']//input[@type='submit']");

	@InjectConfig(name = "google.url")
	private String googleUrl;

	public AdvancedSearchStep() {
		super("google");
	}

	@Override
	protected void executeSteps() {
		openGoogle();
		navigateToAdvancedSearch();
		enterSearchTerm();
		selectLanguage();
		selectCountry();
		submitPage();
		validateSearchResult();
	}

	private void openGoogle() {
		wdt.get(googleUrl);
	}

	private void navigateToAdvancedSearch() {
		wdt.click(BY_SETTINGS_MENU);
		wdt.click(BY_ADVANCED_SEARCH_MENU);
	}

	private void enterSearchTerm() {
		wdt.processField(BY_SEARCH_INPUT, "google", "searchTerm");
	}

	private void selectLanguage() {
		wdt.click(BY_LANGUAGE_DROPDOWN);
		wdt.click(By.id(getDataSet().getValue("languageId")));
	}

	private void selectCountry() {
		wdt.click(BY_COUNTRY_DROPDOWN);
		wdt.click(By.id(getDataSet().getValue("countryId")));
	}

	private void submitPage() {
		wdt.click(BY_SUBMIT_BUTTON);
	}

	private void validateSearchResult() {
		String searchTerm = getDataSet().getValue("searchTerm");
		checkState(webDriver.getPageSource().contains(searchTerm), "Search term '%s' not contained in search result.", searchTerm);
	}
}

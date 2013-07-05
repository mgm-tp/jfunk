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
package com.mgmtp.jfunk.web.step;

import com.mgmtp.jfunk.core.exception.PatternException;
import com.mgmtp.jfunk.core.exception.StepException;

/**
 * Checks if the URL of the current HTML page matches a given regular expression.
 * 
 */
public class CheckUrl extends WebDriverStep {

	private final String regEx;

	/**
	 * Creates a new instance of CheckUrl.
	 * 
	 * @param regEx
	 *            a regular expression which will be checked against the URL of the current HTML
	 *            page
	 */
	public CheckUrl(final String regEx) {
		this.regEx = regEx;
	}

	@Override
	public void execute() throws StepException {
		String url = getWebDriver().getCurrentUrl();
		if (!url.matches(regEx)) {
			throw new PatternException("URL '" + url + "'", regEx, true);
		}
	}
}
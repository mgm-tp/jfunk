/*
 * Copyright (c) 2015 mgm technology partners GmbH
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

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import com.mgmtp.jfunk.core.exception.ValidationException;

/**
 * Searches for a string on the current HTML page.
 * 
 */
public class CheckHtml4String extends WebDriverStep {
	private final String string;
	private final boolean mustExist;
	private final boolean caseSensitive;

	/**
	 * Creates a new instance. Search is case-sensitive. The string is expected to be found.
	 * 
	 * @param string
	 *            the string to be searched for
	 */
	public CheckHtml4String(final String string) {
		this(string, true, true);
	}

	/**
	 * Creates a new instance. The string is expected to be found.
	 * 
	 * @param string
	 *            the string to be searched for
	 * @param caseSensitive
	 *            {@code true} if search is to be case-sensitive
	 */
	public CheckHtml4String(final String string, final boolean caseSensitive) {
		this(string, caseSensitive, true);
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param string
	 *            the string to be searched for
	 * @param caseSensitive
	 *            {@code true} if search is to be case-sensitive
	 * @param mustExist
	 *            if {@code true}, the string must be found, otherwise it must not be found
	 */
	public CheckHtml4String(final String string, final boolean caseSensitive, final boolean mustExist) {
		this.string = string;
		this.caseSensitive = caseSensitive;
		this.mustExist = mustExist;
	}

	@Override
	public void execute() {
		log.info("String '{}' must {}exist in the page source. Search is {}case-sensitive.",
				string, mustExist ? "" : "not ", caseSensitive ? "" : "not ");

		String pageSource = getWebDriver().getPageSource();
		boolean outcome = caseSensitive ? pageSource.contains(string) : containsIgnoreCase(pageSource, string);

		if (mustExist != outcome) {
			if (mustExist) {
				throw new ValidationException("Could not find string '" + string + "' (case-sensitive=" + caseSensitive + ")");
			}
			throw new ValidationException("String '" + string + "' must not occur");
		}
	}
}
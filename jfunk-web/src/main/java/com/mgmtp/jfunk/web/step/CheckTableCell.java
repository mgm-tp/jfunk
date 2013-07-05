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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mgmtp.jfunk.core.exception.StepException;
import com.mgmtp.jfunk.core.exception.ValidationException;
import com.mgmtp.jfunk.web.WebConstants;

/**
 * Finds the table whose id-attribute value equals the given name and checks if the table entry of
 * the given line and column matches the pattern.
 * 
 */
public class CheckTableCell extends WebDriverStep {
	private final String idName;
	private final int rowNumber;
	private final int columnNumber;
	private final Pattern pattern;

	/**
	 * @param idName
	 *            the value of the id-attribute the table has to match
	 * @param rowNumber
	 *            the row number starting with 0, if there is a header it also counts as a row
	 * @param columnNumber
	 *            the column number starting with 0
	 * @param pattern
	 *            a regular expression pattern which must match the entry in the given lineNumber
	 *            and columnNumber of the table
	 */
	public CheckTableCell(final String idName, final int rowNumber, final int columnNumber, final String pattern) {
		this.idName = idName;
		this.rowNumber = rowNumber;
		this.columnNumber = columnNumber;
		this.pattern = Pattern.compile(pattern);
	}

	@Override
	public void execute() {
		WebElement table = findTable(WebConstants.ID, idName);
		List<WebElement> rows = table.findElements(By.tagName(WebConstants.TR));
		if (rowNumber < rows.size()) {
			throw new StepException("Table only had " + rows.size() + " rows; row index however is " + rowNumber);
		}

		List<WebElement> columns = rows.get(rowNumber).findElements(By.tagName(WebConstants.TD));
		WebElement cell = columns.get(columnNumber);
		if (cell == null) {
			throw new StepException("No column " + columnNumber + " in row " + rowNumber);
		}
		String actualValue = cell.getText().trim();
		Matcher m = pattern.matcher(actualValue);
		if (!m.matches()) {
			throw new ValidationException("Value of row " + rowNumber + " and column " + columnNumber
					+ " was not correct: The actual value "
					+ actualValue + " does not match the regular expression " + pattern);
		}
	}

	private WebElement findTable(final String attributeKey, final String attributeValue) {
		List<WebElement> tables = getWebDriver().findElements(By.tagName(WebConstants.TABLE));
		for (WebElement table : tables) {
			String value = table.getAttribute(attributeKey);
			if (StringUtils.equals(attributeValue, value)) {
				return table;
			}
		}
		throw new StepException("Could not find table [" + attributeKey + "=" + attributeValue + "]");
	}
}
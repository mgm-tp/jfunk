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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mgmtp.jfunk.core.exception.ValidationException;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.web.WebConstants;

/**
 * This step should be used when you want to check if some column in HTML table contains some text.
 * 
 */
public class CheckHtmlColumnValue extends WebDriverStep {

	private final String value;
	private final By tableBy;
	private final By columnBy;
	private final boolean isPresent;

	/**
	 * Construct step that will use data from generator
	 * 
	 * @param tableBy
	 *            specifies the table using {@link By}
	 * @param columnBy
	 *            specifies the column using {@link By}
	 * @param dataKey
	 *            key for data in the generator
	 * @param data
	 *            the {@link DataSet} used for getting the value of the {@code dataKey}
	 * @param isPresent
	 *            defines whether the step should check presence of the text or check that given
	 *            text is not present in the column
	 */
	public CheckHtmlColumnValue(final By tableBy, final By columnBy, final String dataKey, final DataSet data,
			final boolean isPresent) {
		this(tableBy, columnBy, checkNotNull(data.getValue(dataKey), "Can't get value from generator for key %s", dataKey),
				isPresent);
	}

	/**
	 * Construct step that will use given text
	 * 
	 * @param tableBy
	 *            specifies the table using {@link By}
	 * @param columnBy
	 *            specifies the column using {@link By}
	 * @param value
	 *            text to check
	 * @param isPresent
	 *            defines whether the step should check presence of the text or check that given
	 *            text is not present in the column
	 */
	public CheckHtmlColumnValue(final By tableBy, final By columnBy, final String value, final boolean isPresent) {
		this.tableBy = tableBy;
		this.columnBy = columnBy;
		this.value = value;
		this.isPresent = isPresent;
	}

	@Override
	public void execute() {
		// wait for rendering the table
		final WebDriverWait tableWait = new WebDriverWait(getWebDriver(), WebConstants.DEFAULT_TIMEOUT);
		tableWait.until(new Function<WebDriver, Boolean>() {

			@Override
			public Boolean apply(final WebDriver input) {
				final WebElement el = input.findElement(tableBy);
				if (el != null && el.isDisplayed() && el.isEnabled()) {
					if (el.getText().contains(value) && isPresent) {
						return true;
					}
					if (!el.getText().contains(value) && !isPresent) {
						return true;
					}
				}
				return false;
			}
		});

		final List<WebElement> column = getWebDriver().findElements(columnBy);
		log.debug("Checking column values with flag isPresent = " + isPresent);
		for (WebElement columnElement : column) {
			log.debug("Checking text: " + columnElement.getText());
			if (value.equals(columnElement.getText())) {
				if (isPresent) {
					log.info("Value '" + value + "' was found in HTML column specified by '" + columnBy);
					return;
				}
				throw new ValidationException("Value '" + value + "' was found in HTML column specified by '" + columnBy);
			}
		}

		if (isPresent) {
			throw new ValidationException("Value '" + value + "' was not found in HTML column specified by '" + columnBy);
		}
		log.info("Value '" + value + "' was not found in HTML column specified by '" + columnBy);
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		tsb.append("isPresent", isPresent);
		tsb.append("tableBy", tableBy);
		tsb.append("columnBy", columnBy);
		tsb.append("value", value);
		return tsb.toString();
	}
}
/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.step;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;
import com.mgmtp.jfunk.core.exception.ValidationException;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.web.WebConstants;

/**
 * This step should be used when you want to check if some column in HTML table contains some text.
 * 
 * @version $Id$
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
	 * @param test
	 *            param no longer used
	 * @param isPresent
	 *            defines whether the step should check presence of the text or check that given
	 *            text is not present in the column
	 */
	@Deprecated
	public CheckHtmlColumnValue(final By tableBy, final By columnBy, final String dataKey, final DataSet data, final TestModule test,
			final boolean isPresent) {
		this(tableBy, columnBy, dataKey, data, isPresent);
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
	 * @param test
	 *            param no longer used
	 * @param isPresent
	 *            defines whether the step should check presence of the text or check that given
	 *            text is not present in the column
	 */
	@Deprecated
	public CheckHtmlColumnValue(final By tableBy, final By columnBy, final String value, final TestModule test, final boolean isPresent) {
		this(tableBy, columnBy, value, isPresent);
	}

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
	public CheckHtmlColumnValue(final By tableBy, final By columnBy, final String dataKey, final DataSet data, final boolean isPresent) {
		this(tableBy, columnBy, checkNotNull(data.getValue(dataKey), "Can't get value from generator for key %s", dataKey), isPresent);
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
		tableWait.until(new Predicate<WebDriver>() {

			@Override
			public boolean apply(final WebDriver input) {
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
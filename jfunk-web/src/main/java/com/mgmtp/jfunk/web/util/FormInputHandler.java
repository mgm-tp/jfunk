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
package com.mgmtp.jfunk.web.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.common.util.ElementTrafo;
import com.mgmtp.jfunk.core.step.base.StepMode;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.web.WebConstants;
import com.mgmtp.jfunk.web.exception.InvalidValueException;

/**
 * <p>
 * A {@link FormInputHandler} facilitates setting and checking values of {@link WebElement}s. The
 * actual action taken depends on the {@link StepMode}.
 * </p>
 * <p>
 * <strong>Warning: {@link FormInputHandler} instances are always immutable</strong>.; Configuration
 * methods have no effect on the instance they are invoked on! You must store and use the new
 * {@link FormInputHandler} instance returned by these methods. This makes {@link FormInputHandler}s
 * thread-safe and safe to store as {@code static final} constants.
 * </p>
 * 
 * <strong>Usage Example:</strong>
 * 
 * <pre>
 * // Create a constant with defaults:
 * public static final FormInputHandler BASE_ELEMENT_HANDER = FormInputHandler.create().enabled(true).displayed(true).timeout(10L, 500L);
 * 
 * // Further refine it with the current web driver, e. g. as a protected field in a base step:
 * public abstract class MyBaseStep extends ComplexStep {
 * 
 * 	&#064;Inject
 * 	protected WebDriver webDriver;
 * 
 * 	protected FormInputHandler elementHandler;
 * 
 * 	&#064;Override
 * 	protected final void executeSteps() {
 * 		elementHandler = BASE_ELEMENT_HANDLER.stepMode(stepMode).webDriver(webDriver).dataSet(getDataSet());
 * 		doExecuteSteps();
 * 	}
 * 
 * 	protected abstract void doExecuteSteps();
 * }
 * 
 * public class SomeStep extends MyBaseStep {
 * 
 * 	&#064;Override
 * 	protected final void doExecuteSteps() {
 * 		elementHandler.by(By.id("field1").dataKey("field1").perform();
 * 		elementHandler.by(By.id("field2").dataKey("field2").perform();
 * 	}
 * }
 * </pre>
 * 
 * <strong>Bad! Do not do this!</strong>
 * 
 * <pre>
 * FormInputHandler handler = FormInputHandler.create().by(someBy);
 * handler.webDriver(webDriver); // does nothing!
 * return handler.perform();
 * </pre>
 * 
 * @author rnaegele
 */
public final class FormInputHandler {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final WebElementFinder finder;
	private final String value;
	private final DataSet dataSet;
	private final String dataKey;
	private final Integer dataIndex;
	private final StepMode stepMode;
	private final DefaultsProvider defaultsProvider;
	private final ElementTrafo setTrafo;
	private final ElementTrafo checkTrafo;

	private FormInputHandler(final WebElementFinder finder, final String value, final DataSet dataSet, final String dataKey,
			final Integer dataIndex, final StepMode stepMode, final DefaultsProvider defaultsProvider,
			final ElementTrafo setTrafo,
			final ElementTrafo checkTrafo) {
		this.finder = finder;
		this.value = value;
		this.dataSet = dataSet;
		this.dataKey = dataKey;
		this.dataIndex = dataIndex;
		this.defaultsProvider = defaultsProvider;
		this.stepMode = stepMode == null ? StepMode.SET_VALUE : stepMode;
		this.setTrafo = setTrafo;
		this.checkTrafo = checkTrafo;
	}

	private FormInputHandler(final Fields fields) {
		this(fields.finder, fields.value, fields.dataSet, fields.dataKey, fields.dataIndex, fields.stepMode,
				fields.defaultsProvider,
				fields.setTrafo, fields.checkTrafo);
	}

	/**
	 * Creates a new instance.
	 * 
	 * @return the new {@link FormInputHandler} instance
	 */
	public static FormInputHandler create() {
		return new FormInputHandler(WebElementFinder.create().noLogging(true), null, null, null, null, null, null, null, null);
	}

	/**
	 * Creates a new {@link FormInputHandler} based on this {@link FormInputHandler} using the
	 * specified {@link WebDriver}.
	 * 
	 * @param theWebDriver
	 *            the WebDriver to use
	 * @return the new {@link FormInputHandler} instance
	 */
	public FormInputHandler webDriver(final WebDriver theWebDriver) {
		Fields fields = new Fields(this);
		fields.finder = fields.finder.webDriver(theWebDriver);
		return new FormInputHandler(fields);
	}

	/**
	 * Creates a new {@link FormInputHandler} based on this {@link FormInputHandler} using the
	 * specified {@link By}.
	 * 
	 * @param theBy
	 *            locates the element to operate on
	 * @return the new {@link FormInputHandler} instance
	 */
	public FormInputHandler by(final By theBy) {
		Fields fields = new Fields(this);
		fields.finder = fields.finder.by(theBy);
		return new FormInputHandler(fields);
	}

	/**
	 * Creates a new {@link FormInputHandler} based on this {@link FormInputHandler} using the
	 * specified timeout.
	 * 
	 * @param theTimeoutSeconds
	 *            the timeout in seconds for waiting for an element to be available
	 * @return the new {@link FormInputHandler} instance
	 */
	public FormInputHandler timeout(final long theTimeoutSeconds) {
		checkArgument(theTimeoutSeconds > 0, "'theTimeoutSeconds' must be greater than zero");
		Fields fields = new Fields(this);
		fields.finder = fields.finder.timeout(theTimeoutSeconds);
		return new FormInputHandler(fields);
	}

	/**
	 * Creates a new {@link FormInputHandler} based on this {@link FormInputHandler} using the
	 * specified timeout.
	 * 
	 * @param theTimeoutSeconds
	 *            the timeout in seconds for waiting for an element to be available
	 * @param theSleepMillis
	 *            the duration in milliseconds to sleep between polls
	 * @return the new {@link FormInputHandler} instance
	 */
	public FormInputHandler timeout(final long theTimeoutSeconds, final long theSleepMillis) {
		checkArgument(theTimeoutSeconds > 0, "'theTimeoutSeconds' must be greater than zero");
		checkArgument(theSleepMillis > 0, "'theSleepMillis' must be greater than zero");
		Fields fields = new Fields(this);
		fields.finder = fields.finder.timeout(theTimeoutSeconds, theSleepMillis);
		return new FormInputHandler(fields);
	}

	/**
	 * Creates a new {@link FormInputHandler} based on this {@link FormInputHandler} that restricts
	 * the enabled status of ele.ments
	 * 
	 * @param theEnabled
	 *            restricts the {@code enabled} status of the element
	 * @return the new {@link FormInputHandler} instance
	 */
	public FormInputHandler enabled(final Boolean theEnabled) {
		Fields fields = new Fields(this);
		fields.finder = fields.finder.enabled(theEnabled);
		return new FormInputHandler(fields);
	}

	/**
	 * Creates a new {@link FormInputHandler} based on this {@link FormInputHandler} that requires
	 * elements to be displayed.
	 * 
	 * @param theDisplayed
	 *            restricts the {@code displayed} status of the element
	 * @return the new {@link FormInputHandler} instance
	 */
	public FormInputHandler displayed(final Boolean theDisplayed) {
		Fields fields = new Fields(this);
		fields.finder = fields.finder.displayed(theDisplayed);
		return new FormInputHandler(fields);
	}

	/**
	 * Creates a new {@link FormInputHandler} based on this {@link FormInputHandler} that requires
	 * elements to be selected.
	 * 
	 * @param theSelected
	 *            restricts the {@code selected} status of the element
	 * @return the new {@link FormInputHandler} instance
	 */
	public FormInputHandler selected(final Boolean theSelected) {
		Fields fields = new Fields(this);
		fields.finder = fields.finder.selected(theSelected);
		return new FormInputHandler(fields);
	}

	/**
	 * Creates a new {@link FormInputHandler} based on this {@link FormInputHandler} using the
	 * specified value to set or check.
	 * 
	 * @param theValue
	 *            the value to set or check
	 * @return the new {@link FormInputHandler} instance
	 */
	public FormInputHandler value(final String theValue) {
		checkState(dataSet == null,
				"Cannot specify value directly because a DataSet is already associated with this FormInputHandler.");

		Fields fields = new Fields(this);
		fields.value = theValue;
		return new FormInputHandler(fields);
	}

	/**
	 * Creates a new {@link FormInputHandler} based on this {@link FormInputHandler} using the
	 * specified {@link DataSet} to retrieve values from.
	 * 
	 * @param theDataSet
	 *            the {@link DataSet} to retrieve the value from
	 * @return the new {@link FormInputHandler} instance
	 */
	public FormInputHandler dataSet(final DataSet theDataSet) {
		checkState(dataSet == null, "Cannot specify a DataSet because a direct value is already set.");

		Fields fields = new Fields(this);
		fields.dataSet = theDataSet;
		return new FormInputHandler(fields);
	}

	/**
	 * Creates a new {@link FormInputHandler} based on this {@link FormInputHandler} using the
	 * specified data key for retrieving the value from the {@link DataSet} associated with this
	 * {@link FormInputHandler}.
	 * 
	 * @param theDataKey
	 *            the data set key used to retrieve the value from the specified data set
	 * @return the new {@link FormInputHandler} instance
	 */
	public FormInputHandler dataKey(final String theDataKey) {
		checkState(dataSet != null, "Cannot specify a data key. Please specify a DataSet first.");

		Fields fields = new Fields(this);
		fields.dataKey = theDataKey;
		return new FormInputHandler(fields);
	}

	/**
	 * Creates a new {@link FormInputHandler} based on this {@link FormInputHandler} using the
	 * specified data key and the specified index for retrieving the value from the {@link DataSet}
	 * associated with this {@link FormInputHandler}.
	 * 
	 * @param theDataKey
	 *            the data set key used to retrieve the value from the specified data set
	 * @param theDataIndex
	 *            the index for retrieving the value from the specified data set
	 * @return the new {@link FormInputHandler} instance
	 */
	public FormInputHandler dataKeyWithIndex(final String theDataKey, final Integer theDataIndex) {
		checkState(dataSet != null, "Cannot specify a data key. Please specify a DataSet first.");

		Fields fields = new Fields(this);
		fields.dataKey = theDataKey;
		fields.dataIndex = theDataIndex;
		return new FormInputHandler(fields);
	}

	/**
	 * Creates a new {@link FormInputHandler} based on this {@link FormInputHandler} using the
	 * specifed {@link StepMode}.
	 * 
	 * @param theStepMode
	 *            the step mode to use; defaults to {@link StepMode#SET_VALUE} if not specified
	 * @return the new {@link FormInputHandler} instance
	 */
	public FormInputHandler stepMode(final StepMode theStepMode) {
		Fields fields = new Fields(this);
		fields.stepMode = theStepMode;
		return new FormInputHandler(fields);
	}

	/**
	 * Creates a new {@link FormInputHandler} based on this {@link FormInputHandler} using the
	 * specifed {@link DefaultsProvider}.
	 * 
	 * @param theDefaultsProvider
	 *            provides default values to check when {@link StepMode} is
	 *            {@link StepMode#CHECK_DEFAULT}
	 * 
	 * @return the new {@link FormInputHandler} instance
	 */
	public FormInputHandler checkDefaults(final DefaultsProvider theDefaultsProvider) {
		Fields fields = new Fields(this);
		fields.defaultsProvider = theDefaultsProvider;
		return new FormInputHandler(fields);
	}

	/**
	 * Creates a new {@link FormInputHandler} based on this {@link FormInputHandler} using the
	 * specified {@link ElementTrafo} for transforming the value before it is set.
	 * 
	 * @param theSetTrafo
	 *            the {@link ElementTrafo} to transform a value before it is set when step mode is
	 *            {@link StepMode#SET_VALUE}
	 * @return the new {@link FormInputHandler} instance
	 */
	public FormInputHandler setTrafo(final ElementTrafo theSetTrafo) {
		Fields fields = new Fields(this);
		fields.setTrafo = theSetTrafo;
		return new FormInputHandler(fields);
	}

	/**
	 * Creates a new {@link FormInputHandler} based on this {@link FormInputHandler} using the
	 * specified {@link ElementTrafo} for transforming the value before it is checked.
	 * 
	 * @param theCheckTrafo
	 *            the {@link ElementTrafo} to transform a value before it is checked when step mode
	 *            is {@link StepMode#CHECK_VALUE}
	 * @return the new {@link FormInputHandler} instance
	 */
	public FormInputHandler checkTrafo(final ElementTrafo theCheckTrafo) {
		Fields fields = new Fields(this);
		fields.checkTrafo = theCheckTrafo;
		return new FormInputHandler(fields);
	}

	/**
	 * Tries to find the field and sets or checks its value depending on the {@link StepMode}.
	 */
	public void perform() {
		log.info(toString());

		WebElement element = finder.find();

		switch (stepMode) {
			case CHECK_VALUE:
				String checkValue = retrieveValue();
				if (checkTrafo != null) {
					checkValue = checkTrafo.trafo(checkValue);
				}
				checkValue(element, checkValue);
				break;

			case CHECK_DEFAULT:
				checkState(defaultsProvider != null, "No DefaultsProvider set when StepMode is CHECK_DEFAULT.");
				checkValue(element, defaultsProvider.get(element, dataSet, dataKey, dataIndex));
				break;

			case SET_VALUE:
				String setValue = retrieveValue();
				if (setTrafo != null) {
					setValue = setTrafo.trafo(setValue);
				}

				if (element.getTagName().equalsIgnoreCase(WebConstants.SELECT)) {

					Select select = new Select(element);
					// First check if a matching value can be found
					List<WebElement> options = select.getOptions();
					boolean found = false;
					for (WebElement option : options) {
						String optionValue = option.getAttribute(WebConstants.VALUE);
						if (StringUtils.equals(optionValue, setValue)) {
							/*
							 * WebElement with matching value could be found --> we are finished
							 */
							found = true;
							select.selectByValue(setValue);
							break;
						}
					}

					if (!found) {
						/*
						 * Fallback: look for a WebElement with a matching visible text
						 */
						for (WebElement option : options) {
							String visibleText = option.getText();
							if (StringUtils.equals(visibleText, setValue)) {
								/*
								 * WebElement with matching value could be found --> we are finished
								 */
								found = true;
								select.selectByVisibleText(setValue);
								break;
							}
						}
					}

					if (!found) {
						throw new JFunkException("Could not find a matching option element in " + element + " , By: "
								+ finder.getBy());
					}

				} else if (WebConstants.INPUT.equalsIgnoreCase(element.getTagName())
						&& WebConstants.RADIO.equals(element.getAttribute(WebConstants.TYPE))) {

					List<WebElement> elements = finder.findAll();
					for (WebElement webElement : elements) {
						String elVal = webElement.getAttribute(WebConstants.VALUE);
						if (elVal.equals(setValue) && !webElement.isSelected()) {
							webElement.click();
						}
					}

				} else if (WebConstants.CHECKBOX.equals(element.getAttribute(WebConstants.TYPE))) {

					if (Boolean.valueOf(setValue) && !element.isSelected() || !Boolean.valueOf(setValue) && element.isSelected()) {
						element.click();
					}

				} else {

					if (element.getAttribute("readonly") == null || element.getAttribute("readonly").equals("false")) {
						element.clear();
						element.sendKeys(setValue);
					} else {
						log.warn("Element is invisible or disabled, value cannot be set");
					}

				}
				break;

			case NONE:
				// do nothing
				break;

			default:
				throw new IllegalArgumentException("Unhandled StepMode=" + stepMode);
		}
	}

	private void checkValue(final WebElement element, final String checkValue) {
		String elementValue = element.getTagName().equalsIgnoreCase(WebConstants.SELECT)
				? new Select(element).getFirstSelectedOption().getAttribute(WebConstants.VALUE)
				: element.getAttribute(WebConstants.VALUE);

		if (WebConstants.INPUT.equalsIgnoreCase(element.getTagName())
				&& WebConstants.RADIO.equals(element.getAttribute(WebConstants.TYPE))) {

			List<WebElement> elements = finder.findAll();
			for (WebElement webElement : elements) {
				String elVal = webElement.getAttribute(WebConstants.VALUE);
				if (elVal.equals(checkValue) && !webElement.isSelected()) {
					throw new InvalidValueException(element, checkValue, elVal);
				}
			}

		} else if (WebConstants.CHECKBOX.equals(element.getAttribute(WebConstants.TYPE))) {

			boolean elVal = element.isSelected();
			if (elVal != Boolean.valueOf(checkValue)) {
				throw new InvalidValueException(element, checkValue, String.valueOf(elVal));
			}

		} else {

			if (!Objects.equal(checkValue, elementValue)) {
				throw new InvalidValueException(element, checkValue, elementValue);
			}

		}
	}

	private String retrieveValue() {
		if (value != null) {
			return value;
		}
		if (dataSet != null && dataKey != null) {
			if (dataIndex != null) {
				return dataSet.getValue(dataKey, dataIndex);
			}
			return dataSet.getValue(dataKey);
		}
		throw new IllegalStateException("Cannot retrieve value. Please set value directly or specify a data set and a data key.");
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		tsb.append("by", finder.getBy());
		tsb.append("value", retrieveValue());
		tsb.append("dataKey", dataKey);
		tsb.append("dataIndex", dataIndex);
		tsb.append("stepMode", stepMode);
		tsb.append("setTrafo", setTrafo);
		tsb.append("checkTrafo", checkTrafo);
		return tsb.toString();
	}

	private static class Fields {
		WebElementFinder finder;
		private String value;
		private DataSet dataSet;
		private String dataKey;
		private Integer dataIndex;
		private StepMode stepMode;
		private DefaultsProvider defaultsProvider;
		private ElementTrafo setTrafo;
		private ElementTrafo checkTrafo;

		private Fields(final FormInputHandler handler) {
			this.finder = handler.finder;
			this.value = handler.value;
			this.dataSet = handler.dataSet;
			this.dataKey = handler.dataKey;
			this.dataIndex = handler.dataIndex;
			this.stepMode = handler.stepMode;
			this.defaultsProvider = handler.defaultsProvider;
			this.setTrafo = handler.setTrafo;
			this.checkTrafo = handler.checkTrafo;
		}
	}

	public static interface DefaultsProvider {
		String get(WebElement element, DataSet dataSet, String dataKey, Integer dataIndex);
	}
}

/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.step;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.util.DataUtils;
import com.mgmtp.jfunk.common.util.ElementTrafo;
import com.mgmtp.jfunk.core.exception.StepException;
import com.mgmtp.jfunk.core.exception.ValidationException;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.core.step.base.StepMode;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.web.WebConstants;
import com.mgmtp.jfunk.web.exception.InvalidValueException;

/**
 * Sets or checks a single HTML element using data from a {@link DataSet} or a fixed value. Several
 * element types are supported:
 * <ul>
 * <li>text fields</li>
 * <li>checkboxes</li>
 * <li>radio buttons</li>
 * <li>drop down lists</li>
 * </ul>
 * When not in either {@link StepMode#SET_VALUE} or {@link StepMode#EDIT_VALUE} or
 * {@link StepMode#SET_EMPTY} the element is checked for a given value (
 * {@link StepMode#CHECK_VALUE} ) or checked for its default value ( {@link StepMode#CHECK_DEFAULT}
 * ).
 * 
 * @version $Id$
 */
public class JFunkWebElement extends WebDriverStep {
	protected final By by;
	protected final String dataKey;
	protected final String dataSetKey;
	protected final ElementTrafo checkTrafo;
	protected final ElementTrafo setTrafo;
	protected final StepMode stepMode;

	private String elementValue;

	@Inject
	protected Map<String, DataSet> dataSets;

	/**
	 * @param by
	 *            By means of this value the HTML element is searched after which will then be
	 *            checked or set.
	 * @param value
	 *            this value will be used to set or check the HTML element against
	 * @param test
	 *            param no longer used
	 * @param stepMode
	 *            the desired {@link StepMode}
	 */
	@Deprecated
	public JFunkWebElement(final By by, final String value, final TestModule test, final StepMode stepMode) {
		this(by, value, stepMode);
	}

	/**
	 * Creates a new instance of JFunkWebElement. This constructor offers the biggest variety of
	 * parameters, several other constructors with fewer parameters are available.
	 * 
	 * @param by
	 *            By means of this value the HTML element is searched after which will then be
	 *            checked or set.
	 * @param dataKey
	 *            When checking or setting the HTML element, this key is used to get the correct
	 *            value from the given {@link DataSet} using {@link DataSet#getValue(String)}
	 * @param data
	 *            the {@link DataSet} used for getting the value of the {@code dataKey}
	 * @param test
	 *            param no longer used
	 * @param stepMode
	 *            the desired {@link StepMode}
	 */
	@Deprecated
	public JFunkWebElement(final By by, final String dataKey, final DataSet data, final TestModule test, final StepMode stepMode) {
		this(by, data.getValue(dataKey), stepMode);
	}

	/**
	 * Creates a new instance of JFunkWebElement. This constructor offers the biggest variety of
	 * parameters, several other constructors with fewer parameters are available.
	 * 
	 * @param by
	 *            By means of this value the HTML element is searched after which will then be
	 *            checked or set.
	 * @param dataKey
	 *            When checking or setting the HTML element, this key is used to get the correct
	 *            value from the given {@link DataSet} using {@link DataSet#getValue(String)} or
	 *            {@link DataSet#getValue(String, int)} if the specified index is non-null
	 * @param index
	 *            used to access indexed {@link DataSet} values
	 * @param data
	 *            the {@link DataSet} used for getting the value of the {@code dataKey}
	 * @param test
	 *            param no longer used
	 * @param stepMode
	 *            the desired {@link StepMode}
	 */
	@Deprecated
	public JFunkWebElement(final By by, final String dataKey, final Integer index, final DataSet data, final TestModule test, final StepMode stepMode) {
		this(by, index == null ? data.getValue(dataKey) : data.getValue(dataKey, index), stepMode);
	}

	/**
	 * Creates a new instance of JFunkWebElement. This constructor offers the biggest variety of
	 * parameters, several other constructors with fewer parameters are available.
	 * 
	 * @param by
	 *            By means of this value the HTML element is searched after which will then be
	 *            checked or set.
	 * @param dataKey
	 *            When checking or setting the HTML element, this key is used to get the correct
	 *            value from the given {@link DataSet} using {@link DataSet#getValue(String)}
	 * @param data
	 *            the {@link DataSet} used for getting the value of the {@code dataKey}
	 * @param test
	 *            param no longer used
	 * @param stepMode
	 *            the desired {@link StepMode}
	 * @param setTrafo
	 *            when in {@link StepMode#SET_VALUE} this {@link ElementTrafo} will be applied at
	 *            first to transform the value before it is set in the form
	 * @param checkTrafo
	 *            when in {@link StepMode#CHECK_VALUE} this {@link ElementTrafo} will be applied at
	 *            first to transform the value before it is checked in the form
	 */
	@Deprecated
	public JFunkWebElement(final By by, final String dataKey, final DataSet data, final TestModule test, final StepMode stepMode,
			final ElementTrafo setTrafo, final ElementTrafo checkTrafo) {
		this(by, data.getValue(dataKey), stepMode, setTrafo, checkTrafo);
	}

	/**
	 * Creates a new instance of JFunkWebElement. This constructor offers the biggest variety of
	 * parameters, several other constructors with fewer parameters are available.
	 * 
	 * @param by
	 *            By means of this value the HTML element is searched after which will then be
	 *            checked or set.
	 * @param dataKey
	 *            When checking or setting the HTML element, this key is used to get the correct
	 *            value from the given {@link DataSet} using {@link DataSet#getValue(String)} or
	 *            {@link DataSet#getValue(String, int)} if the specified index is non-null
	 * @param index
	 *            used to access indexed {@link DataSet} values
	 * @param data
	 *            the {@link DataSet} used for getting the value of the {@code dataKey}
	 * @param test
	 *            param no longer used
	 * @param stepMode
	 *            the desired {@link StepMode}
	 * @param setTrafo
	 *            when in {@link StepMode#SET_VALUE} this {@link ElementTrafo} will be applied at
	 *            first to transform the value before it is set in the form
	 * @param checkTrafo
	 *            when in {@link StepMode#CHECK_VALUE} this {@link ElementTrafo} will be applied at
	 *            first to transform the value before it is checked in the form
	 */
	@Deprecated
	public JFunkWebElement(final By by, final String dataKey, final Integer index, final DataSet data, final TestModule test,
			final StepMode stepMode, final ElementTrafo setTrafo, final ElementTrafo checkTrafo) {
		this(by, index == null ? data.getValue(dataKey) : data.getValue(dataKey, index), stepMode, setTrafo, checkTrafo);
	}

	/**
	 * @param by
	 *            By means of this value the HTML element is searched after which will then be
	 *            checked or set.
	 * @param elementValue
	 *            this value will be used to set or check the HTML element against
	 */
	public JFunkWebElement(final By by, final String elementValue) {
		this(by, elementValue, StepMode.SET_VALUE);
	}

	/**
	 * @param by
	 *            By means of this value the HTML element is searched after which will then be
	 *            checked or set.
	 * @param elementValue
	 *            this value will be used to set or check the HTML element against
	 * @param stepMode
	 *            the desired {@link StepMode}
	 */
	public JFunkWebElement(final By by, final String elementValue, final StepMode stepMode) {
		this(by, elementValue, stepMode, null, null);
	}

	/**
	 * @param by
	 *            By means of this value the HTML element is searched after which will then be
	 *            checked or set.
	 * @param elementValue
	 *            this value will be used to set or check the HTML element against
	 * @param stepMode
	 *            the desired {@link StepMode}
	 * @param setTrafo
	 *            when in {@link StepMode#SET_VALUE} this {@link ElementTrafo} will be applied at
	 *            first to transform the value before it is set in the form
	 * @param checkTrafo
	 *            when in {@link StepMode#CHECK_VALUE} this {@link ElementTrafo} will be applied at
	 *            first to transform the value before it is checked in the form
	 */
	public JFunkWebElement(final By by, final String elementValue, final StepMode stepMode, final ElementTrafo setTrafo, final ElementTrafo checkTrafo) {
		this(by, elementValue, (Integer) null, (String) null, stepMode, setTrafo, checkTrafo);
	}

	/**
	 * Creates a new instance of JFunkWebElement. This constructor offers the biggest variety of
	 * parameters, several other constructors with fewer parameters are available.
	 * 
	 * @param by
	 *            By means of this value the HTML element is searched after which will then be
	 *            checked or set.
	 * @param dataKey
	 *            When checking or setting the HTML element, this key is used to get the correct
	 *            value from the given {@link DataSet} using {@link DataSet#getValue(String)}
	 * @param dataSetKey
	 *            the {@link DataSet} used for getting the value of the {@code dataKey}
	 * @param stepMode
	 *            the desired {@link StepMode}
	 */
	public JFunkWebElement(final By by, final String dataKey, final String dataSetKey, final StepMode stepMode) {
		this(by, dataKey, null, dataSetKey, stepMode, null, null);
	}

	/**
	 * Creates a new instance of JFunkWebElement. This constructor offers the biggest variety of
	 * parameters, several other constructors with fewer parameters are available.
	 * 
	 * @param by
	 *            By means of this value the HTML element is searched after which will then be
	 *            checked or set.
	 * @param dataKey
	 *            When checking or setting the HTML element, this key is used to get the correct
	 *            value from the given {@link DataSet} using {@link DataSet#getValue(String)} or
	 *            {@link DataSet#getValue(String, int)} if the specified index is non-null
	 * @param index
	 *            used to access indexed {@link DataSet} values
	 * @param dataSetKey
	 *            the {@link DataSet} used for getting the value of the {@code dataKey}
	 * @param stepMode
	 *            the desired {@link StepMode}
	 */
	public JFunkWebElement(final By by, final String dataKey, final Integer index, final String dataSetKey, final StepMode stepMode) {
		this(by, dataKey, index, dataSetKey, stepMode, null, null);
	}

	/**
	 * Creates a new instance of JFunkWebElement. This constructor offers the biggest variety of
	 * parameters, several other constructors with fewer parameters are available.
	 * 
	 * @param by
	 *            By means of this value the HTML element is searched after which will then be
	 *            checked or set.
	 * @param dataKey
	 *            When checking or setting the HTML element, this key is used to get the correct
	 *            value from the given {@link DataSet} using {@link DataSet#getValue(String)}
	 * @param dataSetKey
	 *            the {@link DataSet} used for getting the value of the {@code dataKey}
	 * @param stepMode
	 *            the desired {@link StepMode}
	 * @param setTrafo
	 *            when in {@link StepMode#SET_VALUE} this {@link ElementTrafo} will be applied at
	 *            first to transform the value before it is set in the form
	 * @param checkTrafo
	 *            when in {@link StepMode#CHECK_VALUE} this {@link ElementTrafo} will be applied at
	 *            first to transform the value before it is checked in the form
	 */
	public JFunkWebElement(final By by, final String dataKey, final String dataSetKey, final StepMode stepMode, final ElementTrafo setTrafo,
			final ElementTrafo checkTrafo) {
		this(by, dataKey, null, dataSetKey, stepMode, setTrafo, checkTrafo);
	}

	/**
	 * Creates a new instance of JFunkWebElement. This constructor offers the biggest variety of
	 * parameters, several other constructors with fewer parameters are available.
	 * 
	 * @param by
	 *            By means of this value the HTML element is searched after which will then be
	 *            checked or set.
	 * @param dataKey
	 *            When checking or setting the HTML element, this key is used to get the correct
	 *            value from the given {@link DataSet} using {@link DataSet#getValue(String)} or
	 *            {@link DataSet#getValue(String, int)} if the specified index is non-null
	 * @param index
	 *            used to access indexed {@link DataSet} values
	 * @param dataSetKey
	 *            the {@link DataSet} used for getting the value of the {@code dataKey}
	 * @param stepMode
	 *            the desired {@link StepMode}
	 * @param setTrafo
	 *            when in {@link StepMode#SET_VALUE} this {@link ElementTrafo} will be applied at
	 *            first to transform the value before it is set in the form
	 * @param checkTrafo
	 *            when in {@link StepMode#CHECK_VALUE} this {@link ElementTrafo} will be applied at
	 *            first to transform the value before it is checked in the form
	 */
	public JFunkWebElement(final By by, final String dataKey, final Integer index, final String dataSetKey, final StepMode stepMode,
			final ElementTrafo setTrafo, final ElementTrafo checkTrafo) {
		this.by = by;
		this.dataKey = index == null ? dataKey : dataKey + JFunkConstants.INDEXED_KEY_SEPARATOR + index;
		this.dataSetKey = dataSetKey;
		this.stepMode = stepMode;
		this.setTrafo = setTrafo;
		this.checkTrafo = checkTrafo;
	}

	/**
	 * @throws StepException
	 *             <ul>
	 *             <li>if element specified by {@link By} object in the constructor cannot be found</li>
	 *             <li>if a validation error occurred while checking the value of the WebElement
	 *             against the desired value</li>
	 *             </ul>
	 */
	@Override
	public void execute() throws StepException {
		elementValue = retrieveElementValue();

		final WebDriverWait wait = new WebDriverWait(getWebDriver(), WebConstants.DEFAULT_TIMEOUT);
		final WebElement element = wait.until(new Function<WebDriver, WebElement>() {

			@Override
			public WebElement apply(final WebDriver input) {
				List<WebElement> webElements = input.findElements(by);
				if (webElements.isEmpty()) {
					throw new StepException("Could not find any matching element; By=" + by.toString());
				}
				/*
				 * If the search using the By object does find more than one matching element we are
				 * looping through all elements if we find at least one which matches the criteria
				 * below. If not, an exception is thrown.
				 */
				for (WebElement webElement : webElements) {
					if (webElement.isDisplayed()) {
						if (webElement.isEnabled() || !webElement.isEnabled()
								&& (stepMode == StepMode.CHECK_DEFAULT || stepMode == StepMode.CHECK_VALUE)) {
							return webElement;
						}
					}
				}
				throw new StepException("All elements matching by=" + by + " were either invisible or disabled");
			}
		});

		switch (stepMode) {
			case CHECK_DEFAULT:
				// Check only for text input and textarea
				if (element.getTagName().equals(WebConstants.INPUT) && element.getAttribute(WebConstants.TYPE).equals(WebConstants.TEXT)
						|| element.getTagName().equals(WebConstants.TEXTAREA)) {
					log.info(this.toString());
					String value = element.getAttribute(WebConstants.VALUE);
					if (!DataUtils.isDefaultValue(value)) {
						throw new ValidationException("Wrong default value=" + value + " of " + this);
					}
				}
				break;
			case CHECK_VALUE:
				String checkValue = elementValue;
				String value;
				if (element.getTagName().equalsIgnoreCase(WebConstants.SELECT)) {
					Select select = new Select(element);
					value = select.getFirstSelectedOption().getAttribute(WebConstants.VALUE);
				} else {
					value = element.getAttribute(WebConstants.VALUE);
				}

				if (checkTrafo != null) {
					checkValue = checkTrafo.trafo(checkValue);
				}

				log.info(this + ", checkValue=" + checkValue);
				if (WebConstants.INPUT.equalsIgnoreCase(element.getTagName()) && WebConstants.RADIO.equals(element.getAttribute(WebConstants.TYPE))) {
					List<WebElement> elements = getWebDriver().findElements(by);
					for (WebElement webElement : elements) {
						if (webElement.isDisplayed() && webElement.isEnabled()) {
							String elVal = webElement.getAttribute(WebConstants.VALUE);
							if (elVal.equals(checkValue) && !webElement.isSelected()) {
								throw new InvalidValueException(element, checkValue, elVal);
							}
						}
					}
				} else if (WebConstants.CHECKBOX.equals(element.getAttribute(WebConstants.TYPE))) {
					boolean elVal = element.isSelected();
					if (elVal != Boolean.valueOf(checkValue)) {
						throw new InvalidValueException(element, checkValue, String.valueOf(elVal));
					}
				} else {
					if (!Objects.equal(checkValue, value)) {
						throw new InvalidValueException(element, checkValue, value);
					}
				}
				break;
			case SET_EMPTY:
			case SET_VALUE:
				String setValue = elementValue;
				if (setTrafo != null) {
					setValue = setTrafo.trafo(setValue);
				}
				log.info(this + (setTrafo != null ? ", setValue (after trafo)=" + setValue : ""));
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
						throw new StepException("Could not find a matching option element in " + element + " , By: " + by.toString());
					}
				} else if (WebConstants.INPUT.equalsIgnoreCase(element.getTagName())
						&& WebConstants.RADIO.equals(element.getAttribute(WebConstants.TYPE))) {
					List<WebElement> elements = getWebDriver().findElements(by);
					for (WebElement webElement : elements) {
						if (webElement.isDisplayed() && webElement.isEnabled()) {
							String elVal = webElement.getAttribute(WebConstants.VALUE);
							if (elVal.equals(setValue) && !webElement.isSelected()) {
								webElement.click();
							}
						}
					}
				} else if (WebConstants.CHECKBOX.equals(element.getAttribute(WebConstants.TYPE))) {
					if (Boolean.valueOf(setValue) && !element.isSelected() || !Boolean.valueOf(setValue) && element.isSelected()) {
						element.click();
					}
				} else {
					if (element.isDisplayed() && element.isEnabled()
							&& (element.getAttribute("readonly") == null || element.getAttribute("readonly").equals("false"))) {
						element.clear();
						element.sendKeys(setValue);
					} else {
						log.warn("Element is invisible or disabled, value cannot be set");
					}
				}
				break;
			case EDIT_CANCEL:
			case EDIT_VALUE:
				String editValue = elementValue;
				if (setTrafo != null) {
					editValue = setTrafo.trafo(editValue);
				}
				log.info(this + (setTrafo != null ? ", editValue (after trafo)=" + editValue : ""));
				if (element.getTagName().equalsIgnoreCase(WebConstants.SELECT)) {
					Select select = new Select(element);
					select.selectByValue(editValue);
				} else if (WebConstants.INPUT.equalsIgnoreCase(element.getTagName())
						&& WebConstants.RADIO.equals(element.getAttribute(WebConstants.TYPE))) {
					List<WebElement> elements = getWebDriver().findElements(by);
					for (WebElement webElement : elements) {
						if (webElement.isDisplayed() && webElement.isEnabled()) {
							String elVal = webElement.getAttribute(WebConstants.VALUE);
							if (elVal.equals(editValue) && !webElement.isSelected()) {
								webElement.click();
							}
						}
					}
				} else if (WebConstants.CHECKBOX.equals(element.getAttribute(WebConstants.TYPE))) {
					if (Boolean.valueOf(editValue) && !element.isSelected() || !Boolean.valueOf(editValue) && element.isSelected()) {
						element.click();
					}
				} else {
					if (element.isDisplayed() && element.isEnabled()
							&& (element.getAttribute("readonly") == null || element.getAttribute("readonly").equals("false"))) {
						element.clear();
						element.sendKeys(editValue);
					} else {
						log.warn("Element is invisible or disabled, value cannot be reset");
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

	protected String retrieveElementValue() {
		String value;

		if (stepMode == StepMode.SET_EMPTY) {
			value = "";
		} else {
			if (dataSetKey != null) {
				value = dataSets.get(dataSetKey).getValue(dataKey);
			} else {
				// if no data set key is present, the data key is assumed to be the value
				value = dataKey;
			}
		}
		return value;
	}

	/**
	 * @return the dataSetKey
	 */
	public String getDataSetKey() {
		return dataSetKey;
	}

	/**
	 * @return the dataKey
	 */
	public String getDataKey() {
		return dataKey;
	}

	/**
	 * @return the by
	 */
	public By getBy() {
		return by;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		tsb.append("by", by);
		tsb.append("dataSetKey", dataSetKey);
		tsb.append("dataKey", dataKey);
		tsb.append("elementValue", elementValue);
		tsb.append("stepMode", stepMode);
		return tsb.toString();
	}
}

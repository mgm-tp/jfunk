package com.mgmtp.jfunk.web.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.mgmtp.jfunk.common.util.ElementTrafo;
import com.mgmtp.jfunk.core.exception.StepException;
import com.mgmtp.jfunk.core.step.base.StepMode;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.web.WebConstants;
import com.mgmtp.jfunk.web.exception.InvalidValueException;

/**
 * <p>
 * A {@link WebElementHandler} facilitates setting and check values of {@link WebElement}s. The
 * actual action taken depends on the {@link StepMode}.
 * <p>
 * <b>Warning: {@link WebElementHandler} instances are always immutable</b>.; Configuration methods
 * have no effect on the instance they are invoked on! You must store and use the new
 * {@link WebElementHandler} instance returned by these methods. This makes
 * {@link WebElementHandler}s thread-safe and safe to store as {@code static final} constants.
 * </p>
 * 
 * <pre>
 * // Bad! Do not do this!
 * WebElementHandler handler = WebElementHandler.by(someBy);
 * handler.timeout(2000L); // does nothing!
 * return handler.perform(webDriver);
 * </pre>
 * 
 * @author rnaegele
 * @version $Id: $
 */
public class WebElementHandler {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final By by;
	private final long timeoutSeconds;
	private final long sleepMillis;
	private final Boolean enabled;
	private final Boolean displayed;
	private final Boolean selected;
	private final String value;
	private final StepMode stepMode;
	private final ElementTrafo setTrafo;
	private final ElementTrafo checkTrafo;

	private WebElementHandler(final By by, final long timeoutSeconds, final long sleepMillis, final Boolean enabled, final Boolean displayed,
			final Boolean selected, final String value, final StepMode stepMode, final ElementTrafo setTrafo, final ElementTrafo checkTrafo) {
		this.by = by;
		this.timeoutSeconds = timeoutSeconds;
		this.sleepMillis = sleepMillis;
		this.enabled = enabled;
		this.displayed = displayed;
		this.selected = selected;
		this.value = value;
		this.stepMode = stepMode == null ? StepMode.SET_VALUE : stepMode;
		this.setTrafo = setTrafo;
		this.checkTrafo = checkTrafo;
	}

	private WebElementHandler(final Fields fields) {
		this(fields.by, fields.timeoutSeconds, fields.sleepMillis, fields.enabled, fields.displayed, fields.selected, fields.value, fields.stepMode,
				fields.setTrafo, fields.checkTrafo);
	}

	/**
	 * Creates a new instance for the specified {@link By} object.
	 * 
	 * @param by
	 *            locates the element to operate on
	 * @return e new {@link WebElementHandler} instance
	 */
	public static WebElementHandler by(final By by) {
		return new WebElementHandler(by, 0L, 0L, null, null, null, null, null, null, null);
	}

	/**
	 * Creates a new {@link WebElementHandler} based on this {@link WebElementHandler}.
	 * 
	 * @param theTimeoutSeconds
	 *            the timeout in seconds for waiting for an element to be available
	 * @return the new {@link WebElementHandler} instance
	 */
	public WebElementHandler timeout(final long theTimeoutSeconds) {
		Fields fields = new Fields(this);
		fields.timeoutSeconds = theTimeoutSeconds;
		return new WebElementHandler(fields);
	}

	/**
	 * Creates a new {@link WebElementHandler} based on this {@link WebElementHandler}.
	 * 
	 * @param theTimeoutSeconds
	 *            the timeout in seconds for waiting for an element to be available
	 * @param theSleepMillis
	 *            the duration in milliseconds to sleep between polls
	 * @return the new {@link WebElementHandler} instance
	 */
	public WebElementHandler timeout(final long theTimeoutSeconds, final long theSleepMillis) {
		Fields fields = new Fields(this);
		fields.timeoutSeconds = theTimeoutSeconds;
		fields.sleepMillis = theSleepMillis;
		return new WebElementHandler(fields);
	}

	/**
	 * Creates a new {@link WebElementHandler} based on this {@link WebElementHandler}.
	 * 
	 * @param theEnabled
	 *            restricts the {@code enabled} status of the element
	 * @return the new {@link WebElementHandler} instance
	 */
	public WebElementHandler enabled(final Boolean theEnabled) {
		Fields fields = new Fields(this);
		fields.enabled = theEnabled;
		return new WebElementHandler(fields);
	}

	/**
	 * Creates a new {@link WebElementHandler} based on this {@link WebElementHandler}.
	 * 
	 * @param theDisplayed
	 *            restricts the {@code displayed} status of the element
	 * @return the new {@link WebElementHandler} instance
	 */
	public WebElementHandler displayed(final Boolean theDisplayed) {
		Fields fields = new Fields(this);
		fields.displayed = theDisplayed;
		return new WebElementHandler(fields);
	}

	/**
	 * Creates a new {@link WebElementHandler} based on this {@link WebElementHandler}.
	 * 
	 * @param theSelected
	 *            restricts the {@code selected} status of the element
	 * @return the new {@link WebElementHandler} instance
	 */
	public WebElementHandler selected(final Boolean theSelected) {
		Fields fields = new Fields(this);
		fields.selected = theSelected;
		return new WebElementHandler(fields);
	}

	/**
	 * Creates a new {@link WebElementHandler} based on this {@link WebElementHandler}.
	 * 
	 * @param theValue
	 *            the value to set or check
	 * @return the new {@link WebElementHandler} instance
	 */
	public WebElementHandler value(final String theValue) {
		Fields fields = new Fields(this);
		fields.value = theValue;
		return new WebElementHandler(fields);
	}

	/**
	 * Creates a new {@link WebElementHandler} based on this {@link WebElementHandler}.
	 * 
	 * @param dataSet
	 *            the {@link DataSet} to retrieve the value from
	 * @param dataKey
	 *            the data set key used to retrieve the value from the specified data set
	 * @return the new {@link WebElementHandler} instance
	 */
	public WebElementHandler value(final DataSet dataSet, final String dataKey) {
		Fields fields = new Fields(this);
		fields.value = dataSet.getValue(dataKey);
		return new WebElementHandler(fields);
	}

	/**
	 * Creates a new {@link WebElementHandler} based on this {@link WebElementHandler}.
	 * 
	 * @param dataSet
	 *            the {@link DataSet} to retrieve the value from
	 * @param dataKey
	 *            the data set key used to retrieve the value from the specified data set
	 * @param index
	 *            the index for retrieving the value from the specified data set
	 * @return the new {@link WebElementHandler} instance
	 */
	public WebElementHandler value(final DataSet dataSet, final String dataKey, final int index) {
		Fields fields = new Fields(this);
		fields.value = dataSet.getValue(dataKey, index);
		return new WebElementHandler(fields);
	}

	/**
	 * Creates a new {@link WebElementHandler} based on this {@link WebElementHandler}.
	 * 
	 * @param theStepMode
	 *            the step mode to use; defaults to {@link StepMode#SET_VALUE} if not specified
	 * @return the new {@link WebElementHandler} instance
	 */
	public WebElementHandler stepMode(final StepMode theStepMode) {
		Fields fields = new Fields(this);
		fields.stepMode = theStepMode;
		return new WebElementHandler(fields);
	}

	/**
	 * Creates a new {@link WebElementHandler} based on this {@link WebElementHandler}.
	 * 
	 * @param theSetTrafo
	 *            the {@link ElementTrafo} to transform a value before it is set when step mode is
	 *            {@link StepMode#SET_VALUE}
	 * @return the new {@link WebElementHandler} instance
	 */
	public WebElementHandler setTrafo(final ElementTrafo theSetTrafo) {
		Fields fields = new Fields(this);
		fields.setTrafo = theSetTrafo;
		return new WebElementHandler(fields);
	}

	/**
	 * Creates a new {@link WebElementHandler} based on this {@link WebElementHandler}.
	 * 
	 * @param theCheckTrafo
	 *            the {@link ElementTrafo} to transform a value before it is checked when step mode
	 *            is {@link StepMode#CHECK_VALUE}
	 * @return the new {@link WebElementHandler} instance
	 */
	public WebElementHandler checkTrafo(final ElementTrafo theCheckTrafo) {
		Fields fields = new Fields(this);
		fields.checkTrafo = theCheckTrafo;
		return new WebElementHandler(fields);
	}

	/**
	 * Tries to find the field and sets or checks its value depending on the {@link StepMode}.
	 * 
	 * @param webDriver
	 *            the {@link WebDriver}
	 */
	public void perform(final WebDriver webDriver) {
		WebElementFinder finder = WebElementFinder.with(webDriver)
				.enabled(enabled != null && enabled || stepMode == StepMode.CHECK_VALUE)
				.displayed(displayed)
				.selected(selected);

		if (timeoutSeconds > 0L) {
			if (sleepMillis > 0L) {
				finder.timeout(timeoutSeconds, sleepMillis);
			} else {
				finder.timeout(timeoutSeconds);
			}
		}

		WebElement element = finder.find(by);

		switch (stepMode) {
			case CHECK_VALUE:
				String checkValue = value;

				if (checkTrafo != null) {
					checkValue = checkTrafo.trafo(checkValue);
				}

				log.info(this + ", checkValue=" + checkValue);

				String elementValue = element.getTagName().equalsIgnoreCase(WebConstants.SELECT)
						? new Select(element).getFirstSelectedOption().getAttribute(WebConstants.VALUE)
						: element.getAttribute(WebConstants.VALUE);

				if (WebConstants.INPUT.equalsIgnoreCase(element.getTagName()) && WebConstants.RADIO.equals(element.getAttribute(WebConstants.TYPE))) {

					List<WebElement> elements = finder.findAll(by);
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
				break;

			case SET_VALUE:
				String setValue = value;

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

					List<WebElement> elements = finder.findAll(by);
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

	private static class Fields {
		private final By by;
		private long timeoutSeconds;
		private long sleepMillis;
		private Boolean enabled;
		private Boolean displayed;
		private Boolean selected;
		private String value;
		private StepMode stepMode;
		private ElementTrafo setTrafo;
		private ElementTrafo checkTrafo;

		private Fields(final WebElementHandler handler) {
			this.by = handler.by;
			this.timeoutSeconds = handler.timeoutSeconds;
			this.sleepMillis = handler.sleepMillis;
			this.enabled = handler.enabled;
			this.displayed = handler.displayed;
			this.selected = handler.selected;
			this.value = handler.value;
			this.stepMode = handler.stepMode;
			this.setTrafo = handler.setTrafo;
			this.checkTrafo = handler.checkTrafo;
		}
	}
}

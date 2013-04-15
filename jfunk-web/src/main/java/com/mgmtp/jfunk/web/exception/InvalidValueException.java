/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.exception;

import org.openqa.selenium.WebElement;

import com.mgmtp.jfunk.core.exception.ValidationException;
import com.mgmtp.jfunk.core.step.base.StepMode;

/**
 * This exception is thrown when running in {@link StepMode#CHECK_VALUE} and a checked value does
 * not match the expected one.
 * 
 */
public class InvalidValueException extends ValidationException {

	public InvalidValueException(final Object form, final String key, final String expectedValue, final String actualValue) {
		super("Value of element=" + key + " in form=" + form + " was wrong. The actual value=" + actualValue + " does not match the expected value="
				+ expectedValue);
	}

	public InvalidValueException(final String id, final String expectedValue, final String actualValue) {
		super("Value of element with id=" + id + " was wrong. The actual value=" + actualValue + " does not match the expected value="
				+ expectedValue);
	}

	public InvalidValueException(final WebElement webElement, final String expectedValue, final String actualValue) {
		super("Value of element=" + webElement + " was wrong. The actual value=" + actualValue + " does not match the expected value="
				+ expectedValue);
	}
}
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
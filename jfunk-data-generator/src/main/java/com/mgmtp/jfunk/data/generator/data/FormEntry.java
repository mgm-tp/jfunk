/*
 * Copyright (c) 2014 mgm technology partners GmbH
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
package com.mgmtp.jfunk.data.generator.data;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;

import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.generator.constraint.Constraint;
import com.mgmtp.jfunk.data.generator.util.ValueCallback;

/**
 * Advanced key-value-pair. A single form entry always belongs to a {@link FormData} object which is
 * identified through a key. The value of this form entry can either be a fixed value (which is
 * always the same, even after calling {@link #resetValue()} or a regular value which is generated
 * using the underlying constraint.
 * 
 */
public final class FormEntry implements ValueCallback {

	private final Logger log = Logger.getLogger(getClass());

	private final String formDataKey;
	private final String key;
	private String fixedValue;
	private String currentValue;
	private final Constraint constraint;
	private final Configuration configuration;

	/**
	 * Creates a new form entry object. The constructor is protected as new form entry objects may
	 * only be created by a {@link FormData} object.
	 * 
	 * @param formDataKey
	 *            identifies the {@link FormData} object this form entry belongs to
	 * @param key
	 *            identifies the form entry itself. Normally this matches the name of the underlying
	 *            HTML element.
	 * @param value
	 *            a fixed value for this form entry. If set, this value will never be reset. It can
	 *            only be changed by calling {@link #setFixedValue(String)}. If {@code null} the
	 *            value will be generated using the underlying {@link Constraint}.
	 * @param constraint
	 *            the data generator constraint which generates values for this form entry
	 * @param configuration
	 *            contains all properties for the current test run
	 */
	protected FormEntry(final String formDataKey, final String key, final String value, final Constraint constraint, final Configuration configuration) {
		this.formDataKey = formDataKey;
		this.key = key;
		this.constraint = constraint;
		if (constraint != null) {
			constraint.setValueCallback(this);
		}
		this.configuration = configuration;
		setFixedValue(value);
	}

	/**
	 * @return the form entry key which identifies this form entry in its {@link FormData} object
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets a fixed value for this form entry which will only be changed when calling this method
	 * again. Calling {@link #resetValue()} does not change this value (in contrary to using
	 * {@link #setCurrentValue(String)}).
	 * 
	 * @param value
	 *            the value to be set
	 */
	private void setFixedValue(final String value) {
		boolean toBeLogged = false;
		if (log.isDebugEnabled() && (value != null && !value.equals(fixedValue) || fixedValue != null && !fixedValue.equals(value))) {
			toBeLogged = true;
		}
		fixedValue = value;
		currentValue = null;
		if (toBeLogged) {
			log.debug("setFixedValue: " + this);
		}

	}

	/**
	 * Sets a value for this form entry. In contrary to setting a value via
	 * {@link #setFixedValue(String)} this value will not survive a call to {@link #resetValue()}.
	 * 
	 * @param value
	 *            the value to be set
	 */
	public void setCurrentValue(final String value) {
		currentValue = value;
	}

	/**
	 * Returns the value for this form entry. The value is determined as follows:
	 * <ol>
	 * <li>if set, the fixed value will be returned</li>
	 * <li>if set, the current value will be returned</li>
	 * <li>when the underlying constraint exists (i.e. is not {@code null}) it will be used for
	 * generating a value</li>
	 * </ol>
	 * The value could still be null after the three steps mentioned above. If so, an empty string
	 * is returned. Any non empty string will be processed through
	 * {@link Configuration#processPropertyValue(String)} before it is returned.
	 */
	public String getValue() {
		String value = null;
		if (fixedValue != null) {
			value = fixedValue;
		} else if (currentValue != null) {
			value = currentValue;
		} else if (constraint != null) {
			value = constraint.initValues(null);
		}
		if (value == null) {
			value = "";
		}
		return configuration.processPropertyValue(value);
	}

	/**
	 * In contrary to calling {@link #getValue()} this method does not use the underlying constraint
	 * for generating a value. Instead, only fixed value and current value are checked. The returned
	 * value is not processed using {@link Configuration#processPropertyValue(String)} and can also
	 * be null.
	 * 
	 * @see com.mgmtp.jfunk.data.generator.util.ValueCallback#getImmutableValue()
	 */
	@Override
	public String getImmutableValue() {
		String value = null;
		if (fixedValue != null) {
			value = fixedValue;
		} else if (currentValue != null) {
			value = currentValue;
		}
		return value;
	}

	/**
	 * Resets the current value of this form entry. A value set by {@link #setFixedValue(String)} is
	 * not reset.
	 */
	public void resetValue() {
		currentValue = null;
	}

	/**
	 * Returns the underlying generator constraint which is used for generating values for this
	 * constraint if no fixed or current value is set.
	 */
	public Constraint getConstraint() {
		return constraint;
	}

	/**
	 * Convenience method. Uses {@link #getValue()} and tries to parse the value to an integer.
	 * 
	 * @throws NumberFormatException
	 *             if the value could not be converted into an integer
	 * @return the value as an integer
	 */
	public int getInteger() {
		String value = getValue();
		return Integer.parseInt(value);
	}

	/**
	 * Convenience method. Uses {@link #getValue()} and tries to parse the value to a decimal
	 * number.
	 * 
	 * @throws NumberFormatException
	 *             if the value could not be converted into a decimal number
	 * @return the value as a decimal number
	 */
	public double getDouble() {
		String value = getValue();
		return Double.parseDouble(value.replaceAll(",", "."));
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		tsb.append("formDataKey", formDataKey);
		tsb.append("key", key);
		tsb.append("fixedValue", fixedValue);
		tsb.append("currentValue", currentValue);
		return tsb.toString();
	}
}
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
package com.mgmtp.jfunk.data.generator.constraint.base;

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.control.FieldCase;

/**
 * Basic constraint for all constraints that have a status (= value). This means that a value
 * generated once is retained until the next reset and is not recalculated every time
 * {@link #getValue(String, FieldCase)} is called.
 * 
 */
public abstract class BaseStateConstraint extends BaseConstraint {

	private boolean init;
	private String value;

	public BaseStateConstraint(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
		init = false;
	}

	/**
	 * Is usually called by the getValue() method of the constraint class to get the value of the
	 * constraint. However, the method can also be called directly, for example if certain field
	 * have to be generated. Calling the method repeatedly is ineffective as long as no reset has
	 * been executed - the value is retained.
	 */
	@Override
	public final String initValues(final FieldCase c) {
		if (valueCallback != null) {
			String v = valueCallback.getImmutableValue();
			if (v != null) {
				return v;
			}
		}
		if (!init) {
			if (c == FieldCase.BLANK) {
				String v;
				try {
					v = initValuesImpl(FieldCase.AVG);
				} catch (Exception e) {
					throw new IllegalStateException(this + " could not generate value", e);
				}
				if (v != null) {
					StringBuffer b = new StringBuffer(v.length());
					for (int i = 0; i < v.length(); i++) {
						b.append(' ');
					}
					value = b.toString();
				}
			} else {
				try {
					long newToken = generator.getCurrentToken();
					if (token != -1 && token < newToken) {
						throw new IllegalStateException(this + " is called for the second time within its initialization");
					}
					token = newToken;
					value = initValuesImpl(c);
				} catch (Exception e) {
					throw new IllegalStateException(this + " could not generate value", e);
				} finally {
					token = -1;
				}
				if (c == FieldCase.NULL) {
					value = null;
				}
			}
		}
		init = true;
		return value;
	}

	/**
	 * Method is called as needed by the initValues (@see #initValuesImpl).
	 */
	@Override
	protected abstract String initValuesImpl(FieldCase c) throws Exception;

	/**
	 * Resets the value of this constraint and calls the method restValuesImpl. In addition sets the
	 * init Flag to {@code false} and the value to {@code null}. A possibly set fixed value (compare
	 * {@link #setValueCallback(com.mgmtp.jfunk.data.generator.util.ValueCallback)} will not be
	 * reset.
	 */
	@Override
	public final void resetValues() {
		resetValuesImpl();
		value = null;
		init = false;
	}

	/**
	 * This method is called by the method resetValues
	 */
	protected abstract void resetValuesImpl();
}
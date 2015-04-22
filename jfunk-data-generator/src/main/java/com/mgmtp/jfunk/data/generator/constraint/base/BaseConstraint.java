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
package com.mgmtp.jfunk.data.generator.constraint.base;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.Constraint;
import com.mgmtp.jfunk.data.generator.constraint.ConstraintFactory;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.control.FieldControl;
import com.mgmtp.jfunk.data.generator.exception.IdNotFoundException;
import com.mgmtp.jfunk.data.generator.util.CharacterSet;
import com.mgmtp.jfunk.data.generator.util.ValueCallback;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * Implementation of {@link Constraint}. All constraints should extend from this class. Some basic
 * functionality is offered here:
 * <ul>
 * <li>access to constraint id via {@link #getId()}</li>
 * <li>access to last id in hierarchy via {@link #getLastIdInHierarchy()}</li>
 * <li>access to embedded constraint via {@link #getConstraint(Element)}</li>
 * <li>fixed value functionality (see {@link ValueCallback}) and
 * {@link #setValueCallback(ValueCallback)}</li>
 * </ul>
 * 
 */
public abstract class BaseConstraint implements Constraint {
	protected Logger log;

	/**
	 * Reference to the {@link Generator data generator} so that every constraint can use the
	 * methods provided there.
	 */
	protected Generator generator;
	protected ValueCallback valueCallback;
	/**
	 * Attribute "id" from the generator configuration file. May be {@code null}.
	 */
	private String id;

	/**
	 * If a constraint does not have its own id, this variable keeps the last id in hierarchy. This
	 * variable is never {@code null}.
	 */
	private String lastIdInHierarchy;

	/**
	 * This id defines which {@link CharacterSet} will be used when generating values.
	 */
	protected String characterSetId;
	protected long token = -1;

	/**
	 * FIXME Elster specific code
	 */
	protected boolean optional;

	/**
	 * Helper class for generating random values
	 */
	protected final MathRandom random;

	protected BaseConstraint(final MathRandom random, final Element element, final Generator generator) {
		this.random = random;
		log = Logger.getLogger(getClass());
		this.generator = generator;
		Attribute a = element.getAttribute(XMLTags.ID);
		if (a != null) {
			id = a.getValue();
			lastIdInHierarchy = a.getValue();
		} else {
			Element parentElement = element;
			while (lastIdInHierarchy == null) {
				parentElement = parentElement.getParentElement();
				if (parentElement != null) {
					a = parentElement.getAttribute(XMLTags.ID);
					if (a != null) {
						lastIdInHierarchy = a.getValue();
					}
				} else {
					break;
				}
			}
		}
		a = element.getAttribute(XMLTags.CHARSET_ID);
		if (a != null) {
			characterSetId = a.getValue();
		} else {
			Element parentElement = element;
			while (characterSetId == null) {
				parentElement = parentElement.getParentElement();
				if (parentElement != null) {
					a = parentElement.getAttribute(XMLTags.CHARSET_ID);
					if (a != null) {
						characterSetId = a.getValue();
					}
				} else {
					break;
				}
			}
		}

	}

	/**
	 * Returns the {@link FieldControl}-object for this field.
	 * 
	 * @return the {@link FieldControl}-object that was set for this constraint (if one was set)
	 */
	@Override
	public FieldControl getControl() {
		throw new UnsupportedOperationException("No FieldControl set");
	}

	@Override
	public String initValues(final FieldCase c) {
		if (valueCallback != null) {
			String v = valueCallback.getImmutableValue();
			if (v != null) {
				return v;
			}
		}
		try {
			long newToken = generator.getCurrentToken();
			if (token != -1 && token < newToken) {
				throw new IllegalStateException(this + " is called for the second time within its initialization");
			}
			token = newToken;
			return initValuesImpl(c);
		} catch (Exception e) {
			log.error("Error initializing field " + getId());
			throw new IllegalStateException("Error initializing " + this, e);
		} finally {
			token = -1;
		}
	}

	protected abstract String initValuesImpl(FieldCase c) throws Exception;

	/**
	 * This method sets this constraint to a fixed value. This means that no values will be
	 * generated until this value has been reset to Null again.
	 */
	@Override
	public void setValueCallback(final ValueCallback callback) {
		valueCallback = callback;
	}

	@Override
	public final String getId() {
		return id;
	}

	@Override
	public final String getLastIdInHierarchy() {
		return lastIdInHierarchy;
	}

	@Override
	public Set<String> getContainedIds() {
		Set<String> s = new TreeSet<String>();
		if (id != null) {
			s.add(id);
		}
		return s;
	}

	/**
	 * Searches for the constraint child element in the element and gets a constraint from the
	 * factory. If there is no constraint child element, a constraint_ref element is searched and
	 * the constraint is taken from the factory map.
	 * 
	 * @return the constraint contained in this element
	 */
	protected final Constraint getConstraint(final Element element) {
		Element constraintElement = element.getChild(XMLTags.CONSTRAINT);
		if (constraintElement != null) {
			return generator.getConstraintFactory().createModel(random, constraintElement);
		}
		constraintElement = element.getChild(XMLTags.CONSTRAINT_REF);
		if (constraintElement != null) {
			try {
				return generator.getConstraintFactory().getModel(constraintElement);
			} catch (IdNotFoundException e) {
				log.error("Could not find constraint in map. Maybe it has not been initialised;"
						+ " in this case, try rearranging order of constraints in the xml file.", e);
			}
		}
		throw new IllegalStateException("No element constraint or constraint_ref could be found for " + this);
	}

	/**
	 * Method searches the constraint for the given key, initializes it with the passed FieldCase
	 * and returns this value.
	 * 
	 * @return return the value of the constrain for the passed key. If the value is {@code null}
	 *         return an empty string
	 */
	protected final String getValue(final String key, final FieldCase ca) throws IdNotFoundException {
		ConstraintFactory f = generator.getConstraintFactory();
		String value = f.getModel(key).initValues(ca);
		if (value == null) {
			return "";
		}
		return value;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		tsb.append("id", id);
		tsb.append("lastIdInHierarchy", lastIdInHierarchy);
		tsb.append("fixedValue", valueCallback == null ? null : valueCallback.getImmutableValue());
		return tsb.toString();
	}
}
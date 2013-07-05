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
package com.mgmtp.jfunk.data.generator.field;

import org.apache.log4j.Logger;
import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;
import com.mgmtp.jfunk.data.generator.control.FieldCase;

/**
 * Field instances are responsible for the generation of values. These can have different classes to
 * for example create date or numeric fields. A field is embedded into a constraint that constitutes
 * the rules for the field or a collection of some fields. A field only generates a value subject to
 * the specified parameters.
 * 
 */
public abstract class Field {

	protected final Logger log = Logger.getLogger(getClass());
	protected final MathRandom random;
	protected final Element element;
	protected String characterSetId;

	public Field(final MathRandom random, final Element element, final String characterSetId) {
		this.random = random;
		this.element = element;
		this.characterSetId = characterSetId;
	}

	/**
	 * Method returns a value subject to the type of the field and the specified FieldCase object.
	 */
	public abstract String getString(FieldCase param);

	/**
	 * Returns thr range of lengths valid for this field. If it is an enumeration type, this method
	 * returns an intervall [0,(x-1)] where x is the number of values of this enumeration type. d
	 */
	public abstract Range getRange();

	/**
	 * Sets the range of this field. It can for example be used by a range constraint to limit a
	 * field.
	 */
	public abstract void setRange(Range range);

	/**
	 * @return the maximum length of a character string this field can generate or -1 if this length
	 *         can not be established.
	 */
	public abstract int getMaxLength();
}
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
package com.mgmtp.jfunk.data.generator.constraint;

import java.text.ParseException;

import com.ibm.icu.text.NumberFormat;
import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.base.SourceConstraint;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.FormatFactory;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * This constraint object contains a reference to a source constraint and returns as value the value
 * of the source constraint multiplied by a factor. The factor is defined by the sub-element factor
 * in the XML.
 * <p>
 * Example:
 * 
 * <pre>
 * {@code
 * <constraint class="com.mgmtp.jfunk.data.generator.constraint.Multiplier">
 *   <factor>0,5</factor>
 *   <constraint_ref id="..."/>
 * </constraint>
 * }
 * </pre>
 * 
 */
public class Multiplier extends SourceConstraint {

	private final NumberFormat format;
	private final double factor;

	public Multiplier(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
		format = FormatFactory.getNumberFormat(element);
		final String factorString = element.getChildText(XMLTags.FACTOR);
		try {
			final Number number = format.parse(factorString);
			factor = number.doubleValue();
		} catch (ParseException e) {
			throw new IllegalStateException("Could not parse " + factorString + " to a number", e);
		}
	}

	/**
	 * Initializes the source constraint first. Then the source constraint's value is parsed with
	 * the format object of this constraint to a double number and this value is subsequently
	 * multiplied with the configurable factor. The multiplied value is finally formatted to a
	 * string using the the format object again and stored as a value.
	 * 
	 * @return the value just generated
	 */
	@Override
	protected String initValuesImpl(final FieldCase c) {
		double d = 0.0;
		String v = source.initValues(c);
		if (v != null && v.length() > 0) {
			d = 0.0;
			try {
				Number n = format.parse(v);
				d = n.doubleValue();
			} catch (ParseException e) {
				log.debug("Could not parse string " + v + " to a number; setting value 0.0");
			}
		}
		d *= factor;
		return format.format(d);
	}
}
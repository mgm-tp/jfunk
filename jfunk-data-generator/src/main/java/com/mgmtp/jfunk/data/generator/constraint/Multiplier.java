/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.constraint;

import java.text.NumberFormat;
import java.text.ParseException;

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
 * @version $Id$
 */
public class Multiplier extends SourceConstraint {

	private final NumberFormat format;
	private final double factor;

	public Multiplier(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
		format = FormatFactory.getNumberFormat(element);
		String factorString = element.getChildText(XMLTags.FACTOR);
		Number number = null;
		try {
			number = format.parse(factorString);
		} catch (ParseException e) {
			throw new IllegalStateException("Could not parse " + factorString + " to a number", e);
		}
		factor = number.doubleValue();
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
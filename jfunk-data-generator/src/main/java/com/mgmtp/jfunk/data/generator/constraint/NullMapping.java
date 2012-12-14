/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.constraint;

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.base.SourceConstraint;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * The constraint maps a null value of an embedded constraint to a fixed value.
 * <p>
 * Example:
 * 
 * <pre>
 * {@code 
 * <constraint id="c1" class="com.mgmtp.jfunk.data.generator.constraint.NullMapping">
 *   <constant>0,00</constant>
 *   <constraint>
 *     <constraint_ref id="..."/>
 *   </constraint>
 * </constraint>
 * }
 * </pre>
 * 
 * If the embedded constraint returns {@code null}, c1 will be set to 0,00. Otherwise the value of
 * the embedded constraint is used as the value for c1.
 * 
 * @version $Id$
 */
public class NullMapping extends SourceConstraint {

	private final String defaultValue;

	public NullMapping(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
		defaultValue = element.getChildText(XMLTags.CONSTANT);
	}

	/**
	 * Calls {@link #initValues(FieldCase)} on the source constraint. If this returns {@code null}
	 * the defaulValue will be returned
	 */
	@Override
	public String initValuesImpl(final FieldCase c) {
		if (c == FieldCase.NULL) {
			return null;
		}
		String value = source.initValues(c);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}
}
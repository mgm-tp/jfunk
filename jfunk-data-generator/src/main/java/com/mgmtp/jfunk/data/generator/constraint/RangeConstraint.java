/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.constraint;

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * A special {@link FieldContainer} with a minimal and a maximal length.
 * 
 * @version $Id$
 */
public class RangeConstraint extends FieldContainer {
	public RangeConstraint(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
		Range range = field.getRange();
		String minS = element.getChildTextTrim(XMLTags.MIN);
		String maxS = element.getChildTextTrim(XMLTags.MAX);
		int min = minS.length() == 0 ? range.getMin() : Integer.parseInt(minS);
		int max = maxS.length() == 0 ? range.getMax() : Integer.parseInt(maxS);
		Range r = new Range(min, max);
		field.setRange(r);
		control = generator.getControlFactory().getControl(random, element, r);
	}

	@Override
	protected String initValuesImpl(FieldCase ca) {
		ca = control.getNext(ca);
		return super.initValuesImpl(ca);
	}
}
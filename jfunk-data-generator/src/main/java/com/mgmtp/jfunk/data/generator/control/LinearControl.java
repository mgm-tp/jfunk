/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.control;

import java.util.List;

import org.jdom.Element;

import com.google.common.collect.Lists;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;

/**
 * Returns a case for every allowed value. Also if the range at generation is [0,5] then this
 * control object returns the cases {0,1,2,3,4,5}.
 * 
 * @version $Id$
 */
public class LinearControl extends BaseFieldControl {

	public LinearControl(final MathRandom random, final Element element, final Range range) {
		super(random, element, range);
	}

	/**
	 * Generates a FieldCase [min, min + 1, ... , max - 1, max] for every value within the allowed
	 * range.
	 * 
	 * @return a list that contains a FieldCase for every allowed value
	 */
	@Override
	protected List<FieldCase> createCases() {
		List<FieldCase> list = Lists.newArrayListWithExpectedSize(range.getRange() * 2 + 2);
		for (int i = range.getMin(); i <= range.getMax(); i++) {
			list.add(new FieldCase(i));
		}
		return list;
	}

	/**
	 * Returns the number of all allowed values, i.e. max - min + 1
	 * 
	 * @return max - min + 1
	 */
	@Override
	public int countCases() {
		return range.getRange() + 1;
	}

	@Override
	protected FieldCase createCase(FieldCase ca) {
		FieldCase superCase = super.createCase(ca);
		if (ca != superCase) {
			/*
			 * If a special case has been generated this control creates an average case
			 */
			ca = new FieldCase(FieldCase.AVG.getInstance(this).getSize(), ca.getBad());
		}
		return ca;
	}
}
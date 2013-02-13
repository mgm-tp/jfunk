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
 * Extends {@link BaseFieldControl}. Returns a minimal case, an average and a maximal case.
 * 
 * @version $Id$
 */
public class BoundaryControl extends BaseFieldControl {

	public BoundaryControl(final MathRandom random, final Element el, final Range r) {
		super(random, el, r);
	}

	/**
	 * Generates a list with three {@link FieldCase}-objects which are with a uniform distribution
	 * within the valid interval [min, avg, max]
	 */
	@Override
	protected List<FieldCase> createCases() {
		List<FieldCase> s = Lists.newArrayList();
		s.add(new FieldCase(range.getMin()));
		// make sure there are three different sizes in the list
		int rndValue = range.getMin() + 1 + random.getInt(range.getRange() - 2);
		s.add(new FieldCase(rndValue));
		s.add(new FieldCase(range.getMax()));
		return s;
	}

	/**
	 * @return 3 always
	 */
	@Override
	public int countCases() {
		return 3;
	}
}
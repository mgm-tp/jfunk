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
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * Returns a configurable number of cases within the allowed range. Every case is a random value
 * within the allowed range ({@link FieldCase#AVG}).
 * 
 */
public class MathRandomControl extends BaseFieldControl {

	private final int caseCount;

	public MathRandomControl(final MathRandom random, final Element el, final Range r) {
		super(random, el, r);
		caseCount = Integer.parseInt(el.getChildText(XMLTags.CASES));
	}

	@Override
	protected List<FieldCase> createCases() {
		List<FieldCase> list = Lists.newArrayListWithExpectedSize(caseCount);
		while (list.size() < caseCount) {
			list.add(createCase(FieldCase.AVG));
		}
		return list;
	}

	@Override
	public int countCases() {
		return caseCount;
	}
}
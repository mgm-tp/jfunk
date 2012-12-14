/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.constraint;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Element;

import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.base.SourceConstraint;
import com.mgmtp.jfunk.data.generator.control.FieldCase;

/**
 * This constraint gets a sub-constraint. If the sub-constraint returns a value unequal null, the
 * constraint returns the value {@code true}, if not the value {@code false}. Can be used with a
 * mapping constraint.
 * <p>
 * Example:
 * 
 * <pre>
 * {@code
 * <constraint class="com.mgmtp.jfunk.data.generator.constraint.Exists">
 *   <constraint_ref id="constraint_id"/>
 * </constraint>
 * }
 * </pre>
 * 
 * @version $Id$
 */
public class Exists extends SourceConstraint {

	public Exists(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
	}

	@Override
	protected String initValuesImpl(final FieldCase c) throws Exception {
		String value = source.initValues(c);
		if (StringUtils.isNotEmpty(value)) {
			return JFunkConstants.TRUE;
		}
		return JFunkConstants.FALSE;
	}

	/**
	 * Always returns 1.
	 */
	@Override
	public int countCases() {
		return 1;
	}

	/**
	 * Always returns false.
	 */
	@Override
	public boolean hasNextCase() {
		return false;
	}
}
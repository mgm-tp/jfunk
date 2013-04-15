/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.constraint;

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.base.SourceConstraint;
import com.mgmtp.jfunk.data.generator.control.FieldCase;

/**
 * This constraint executes a reset not directly but delays it until initValues() is called the next
 * time. Usually the reset is executed directly and this lead to loss of the values. For example
 * within a mapping constrain all referenced entry constraints are reset at reset. To avoid this if
 * it shall for example be achieved that only the selected constraint is reset and not all, this
 * constraint offers an option. The reset is stored and will be effectively executed with a delay.
 * 
 */
public class OnDemandReset extends SourceConstraint {

	private boolean pendingReset;

	public OnDemandReset(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
		pendingReset = false;
	}

	@Override
	protected String initValuesImpl(final FieldCase c) throws Exception {
		if (pendingReset) {
			pendingReset = false;
			source.resetValues();
		}
		return super.initValuesImpl(c);
	}

	@Override
	public void resetValues() {
		pendingReset = true;
	}
}

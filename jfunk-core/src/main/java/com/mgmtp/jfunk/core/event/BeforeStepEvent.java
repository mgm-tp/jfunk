/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.event;

import com.mgmtp.jfunk.core.step.base.Step;

/**
 * @author rnaegele
 */
public class BeforeStepEvent extends StepEvent {

	public BeforeStepEvent(final Step step, final int index) {
		super(step, index);
	}
}

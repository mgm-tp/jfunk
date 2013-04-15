/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.scripting;

import com.mgmtp.jfunk.core.step.base.Step;

/**
 * {@link InternalBeforeStepEvent} for internal package-local use only. Is posted before the regular
 * {@link InternalBeforeStepEvent}.
 * 
 * @author rnaegele
 */
class InternalBeforeStepEvent extends InternalStepEvent {

	public InternalBeforeStepEvent(final Step step, final int index) {
		super(step, index);
	}
}

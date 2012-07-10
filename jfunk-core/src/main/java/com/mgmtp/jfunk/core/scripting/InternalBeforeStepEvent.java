package com.mgmtp.jfunk.core.scripting;

import com.mgmtp.jfunk.core.step.base.Step;

/**
 * {@link InternalBeforeStepEvent} for internal package-local use only. Is posted before the regular
 * {@link InternalBeforeStepEvent}.
 * 
 * @author rnaegele
 * @version $Id$
 */
class InternalBeforeStepEvent extends InternalStepEvent {

	public InternalBeforeStepEvent(final Step step, final int index) {
		super(step, index);
	}
}

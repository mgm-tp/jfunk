package com.mgmtp.jfunk.core.event;

import com.mgmtp.jfunk.core.step.base.Step;

/**
 * @author rnaegele
 * @version $Id$
 */
public class BeforeStepEvent extends StepEvent {

	public BeforeStepEvent(final Step step, final int index) {
		super(step, index);
	}
}

package com.mgmtp.jfunk.core.scripting;

import com.mgmtp.jfunk.core.step.base.Step;

/**
 * 
 * @author rnaegele
 * @version $Id$
 */
abstract class InternalStepEvent {

	private final Step step;
	private final int index;

	public InternalStepEvent(final Step step, final int index) {
		this.step = step;
		this.index = index;
	}

	/**
	 * @return the step
	 */
	public Step getStep() {
		return step;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
}
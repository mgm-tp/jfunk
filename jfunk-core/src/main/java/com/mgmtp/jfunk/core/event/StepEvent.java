package com.mgmtp.jfunk.core.event;

import com.mgmtp.jfunk.core.step.base.Step;

/**
 * @author rnaegele
 * @version $Id$
 */
public abstract class StepEvent extends AbstractBaseEvent {
	private final Step step;
	private final int index;

	/**
	 *
	 */
	public StepEvent(final Step step, final int index) {
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

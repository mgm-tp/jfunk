package com.mgmtp.jfunk.core.event;

import com.mgmtp.jfunk.core.step.base.Step;

/**
 * @author rnaegele
 * @version $Id$
 */
public class AfterStepEvent extends StepEvent {

	private final Throwable throwable;

	public AfterStepEvent(final Step step, final int index, final Throwable throwable) {
		super(step, index);
		this.throwable = throwable;
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return throwable == null;
	}

	/**
	 * @return the throwable
	 */
	public Throwable getThrowable() {
		return throwable;
	}
}

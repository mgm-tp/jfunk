/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.scripting;

import com.mgmtp.jfunk.core.step.base.Step;

/**
 * {@link InternalAfterStepEvent} for internal package-local use only. Is posted before the regular
 * {@link InternalAfterStepEvent}.
 * 
 * @author rnaegele
 * @version $Id$
 */
class InternalAfterStepEvent extends InternalStepEvent {

	private final Throwable throwable;

	public InternalAfterStepEvent(final Step step, final int index, final Throwable throwable) {
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

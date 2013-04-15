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

/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
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
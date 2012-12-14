/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.control;

import com.mgmtp.jfunk.common.random.Choice;
import com.mgmtp.jfunk.common.random.MathRandom;

/**
 * This class controlls a boolean field. There are the cases true or false
 * 
 * @version $Id$
 */
public class BooleanControl {

	private Choice choice;
	private final MathRandom random;

	public BooleanControl(final MathRandom random) {
		this.random = random;
		reset();
	}

	/**
	 * Returns the value true, if true or false has not been returned by the method
	 * {@link #isNext()}
	 */
	public boolean hasNext() {
		return !choice.isAllHit();
	}

	/**
	 * Returns the values true or false in uniform distribution
	 */
	public boolean isNext() {
		return choice.get();
	}

	/**
	 * Resets this instance to the initial status so hasNext() will return true again.
	 */
	public final void reset() {
		this.choice = new Choice(random);
	}
}
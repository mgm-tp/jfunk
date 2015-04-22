/*
 * Copyright (c) 2015 mgm technology partners GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mgmtp.jfunk.data.generator.control;

import com.mgmtp.jfunk.common.random.Choice;
import com.mgmtp.jfunk.common.random.MathRandom;

/**
 * This class controlls a boolean field. There are the cases true or false
 * 
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
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
package com.mgmtp.jfunk.data.generator.data;

import com.mgmtp.jfunk.common.random.MathRandom;

/**
 */
public class GeneratorState {
	private MathRandom mathRandom;

	protected static final ThreadLocal<GeneratorState> STATE_HOLDER = new ThreadLocal<GeneratorState>() {
		@Override
		protected GeneratorState initialValue() {
			return new GeneratorState();
		}
	};

	public static GeneratorState currentState() {
		return STATE_HOLDER.get();
	}

	/**
	 * Intializes the MathRandom instance.
	 */
	public long initMathRandom(final Long seed) {
		if (seed == null) {
			mathRandom = new MathRandom();
		} else {
			mathRandom = new MathRandom(seed);
		}
		return mathRandom.getSeed();
	}

	public MathRandom getMathRandom() {
		return mathRandom;
	}
}
/*
 * Copyright (c) 2014 mgm technology partners GmbH
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
package com.mgmtp.jfunk.common.random;

import com.mgmtp.jfunk.common.util.Range;

/**
 * Extension of {@link RandomCollection}. The collection contains integer numbers without gaps.
 * 
 */
public class RandomIntegerCollection extends RandomCollection<Integer> {

	/**
	 * Creates a new RandomCollection. The underlying list contains all integer numbers from 0 to
	 * {@code max}.
	 */
	public RandomIntegerCollection(final MathRandom random, final int max) {
		super(random, new Range(0, max).listValues());
	}

	/**
	 * Creates a new RandomCollection. The underlying list contains all integer numbers which are
	 * included in the specified {@link Range} object i.e. from {@link Range#getMin()} to
	 * {@link Range#getMax()}.
	 */
	public RandomIntegerCollection(final MathRandom random, final Range range) {
		super(random, range.listValues());
	}
}
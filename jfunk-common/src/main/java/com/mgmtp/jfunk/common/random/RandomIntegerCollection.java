/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.common.random;

import com.mgmtp.jfunk.common.util.Range;

/**
 * Extension of {@link RandomCollection}. The collection contains integer numbers without gaps.
 * 
 * @version $Id$
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
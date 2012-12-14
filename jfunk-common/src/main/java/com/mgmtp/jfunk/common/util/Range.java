/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.common.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.ranges.RangeException;

/**
 * Helper Class that encapsulates a range of lengths or values, respectively.
 * 
 * @version $Id$
 */
public class Range {

	public static final int RANGE_MAX = 1000;

	private final int min;
	private final int max;

	/**
	 * Creates a range object
	 * 
	 * @param min
	 *            the minimum for this range
	 * @param max
	 *            the maximum for this range
	 */
	public Range(final int min, int max) {
		if (max == -1) {
			max = RANGE_MAX;
		}
		this.min = Math.min(min, max);
		this.max = Math.max(min, max);
	}

	/**
	 * Merges this range with the passed range. Returns a range object whose min and max values
	 * match the minimum or maximum of the two values, respectively. So the range returned contains
	 * this and the passed range in any case.
	 * 
	 * @return a range object with [min(this.min,other.min), max(this.max, other.max)]
	 */
	public Range merge(final Range otherRange) {
		int newMin = Math.min(otherRange.min, min);
		int newMax = Math.max(otherRange.max, max);
		return new Range(newMin, newMax);
	}

	/**
	 * Sums the boundaries of this range with the ones of the passed. So the returned range has
	 * this.min + plus.min as minimum and this.max + plus.max as maximum respectively.
	 * 
	 * @return a range object whose boundaries are summed
	 */
	public Range sumBoundaries(final Range plus) {
		int newMin = min + plus.min;
		int newMax = max == RANGE_MAX || plus.max == RANGE_MAX ? RANGE_MAX : max + plus.max;
		return new Range(newMin, newMax);
	}

	/**
	 * Intersects this range with the one passed. This means that the returned rand contains the
	 * maximum of both minima as minimum and the minimum of the two maxima as maximum.
	 * 
	 * @throws RangeException
	 *             if the given range has not intersection with this range
	 */
	public Range intersect(final Range outerRange) throws RangeException {
		if (min > outerRange.max) {
			throw new IllegalArgumentException("range maximum must be greater or equal than " + min);
		}
		if (max < outerRange.min) {
			throw new IllegalArgumentException("range minimum must be less or equal than " + max);
		}
		int newMin = Math.max(min, outerRange.min);
		int newMax = Math.min(max, outerRange.max);
		return new Range(newMin, newMax);
	}

	/**
	 * Returns max - min
	 * 
	 * @return max - min
	 */
	public int getRange() {
		return max - min;
	}

	/**
	 * @return min
	 */
	public int getMin() {
		return min;
	}

	/**
	 * @return max
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Returns true if minimum equals maximum
	 * 
	 * @return minimum == maximum
	 */
	public boolean isZeroRange() {
		return min == max;
	}

	/**
	 * Returns [this.min,this.max]
	 * 
	 * @return [this.min, this.max]
	 */
	@Override
	public String toString() {
		return "[" + min + "," + max + "]";
	}

	/**
	 * Returns a list of all allowed values within the range
	 * 
	 * @return a list of all allowed values within the range
	 */
	public List<Integer> listValues() {
		ArrayList<Integer> list = new ArrayList<Integer>(getRange());
		for (int i = getMin(); i <= getMax(); i++) {
			list.add(i);
		}
		return list;
	}
}
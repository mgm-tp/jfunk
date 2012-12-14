/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.common.random;

/**
 * Interface for accessing a list of arbitrary elements randomly.
 * 
 * @version $Id$
 */
public interface Randomizable<E> {

	/**
	 * Return the next randomly chosen element
	 */
	E get();

	/**
	 * Returns true if all included elements were returned at least once.
	 */
	boolean isAllHit();

	/**
	 * Reset to original state.
	 */
	void reset();

	/**
	 * Returns the number of handled elements.
	 */
	int size();
}
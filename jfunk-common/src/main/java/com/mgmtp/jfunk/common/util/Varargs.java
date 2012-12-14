/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.common.util;

/**
 * Utility class for transforming var args into object arrays.
 * 
 * @author rnaegele
 * @version $Id$
 */
public final class Varargs {

	private Varargs() {
		// don't allow instantiation
	}

	/**
	 * Creates an object array from the specified var args arguments.
	 * 
	 * @param args
	 *            the arguments
	 * @return the object array
	 */
	public static <T> T[] va(final T... args) {
		return args;
	}
}
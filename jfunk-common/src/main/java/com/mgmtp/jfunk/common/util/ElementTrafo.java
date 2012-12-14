/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.common.util;

/**
 * Transforms a given value and returns the transformed result.
 * 
 * @version $Id$
 */
public interface ElementTrafo {
	/**
	 * @param input
	 *            string which will be transformed
	 * @return the transformed string
	 */
	String trafo(String input);
}
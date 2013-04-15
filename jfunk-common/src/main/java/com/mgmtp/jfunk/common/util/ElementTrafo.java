/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.common.util;

/**
 * Transforms a given value and returns the transformed result.
 * 
 */
public interface ElementTrafo {
	/**
	 * @param input
	 *            string which will be transformed
	 * @return the transformed string
	 */
	String trafo(String input);
}
/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.data;

/**
 * Classes used for manipulating {@link Field Fields} in {@link FieldSet FieldSets} after the
 * generation must implement this interface and are then called with the so far generated
 * {@link FormData}.
 * 
 * @version $Id$
 */
public interface FieldGenerator {
	/**
	 * Called when the {@link FieldSet} the {@link Field} belongs to is generated completely.
	 * 
	 * @param formData
	 *            the so far generated {@link FormData}
	 */
	void generate(FormData formData);
}
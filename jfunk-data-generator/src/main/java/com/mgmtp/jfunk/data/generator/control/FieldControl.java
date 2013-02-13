/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.control;

import com.mgmtp.jfunk.common.util.Range;

/**
 * Interface for different control types used by the constraints to control the generation of
 * fields.
 * 
 * @version $Id$
 */
public interface FieldControl {

	/**
	 * Returns the next mandatory case (e.g. min, max, avg) or a random value if all have already
	 * been requested.
	 * 
	 * @param input
	 *            field case controls the next case for this control objekt
	 * @return a field case object, containing the size and bad parameter
	 */
	FieldCase getNext(FieldCase input);

	/**
	 * Resets this control object to its initial status. Then the mandatory cases will be re-run.
	 */
	void reset();

	/**
	 * Returns true of this field control object still contains mandatory cases. If false, this does
	 * not necessarily mean that getNext() will no longer work, but simply that all mandatory cases
	 * have been run.
	 */
	boolean hasNext();

	/**
	 * Returns the number of mandatory cases for this control object
	 */
	int countCases();

	/**
	 * Returns the allowed range of values in the form of a range object
	 */
	Range getRange();
}
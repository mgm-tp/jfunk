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
package com.mgmtp.jfunk.data.generator.control;

import com.mgmtp.jfunk.common.util.Range;

/**
 * Interface for different control types used by the constraints to control the generation of
 * fields.
 * 
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
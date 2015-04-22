/*
 * Copyright (c) 2015 mgm technology partners GmbH
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
package com.mgmtp.jfunk.data.generator.constraint;

import java.util.Set;

import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.control.FieldControl;
import com.mgmtp.jfunk.data.generator.util.ValueCallback;

/**
 * The data generator generates values which are then used to fill web forms. These values are
 * subject to certain conditions which are defined by using {@link Constraint Constraints}. <br>
 * There are simple constraints, e.g. a constraint representing a drop-down box simply contains the
 * allowed values. However, constraints can get quite complex, e.g. depend on other constraints or
 * combine different constraints. <br>
 * All available constraints reside in this Java package. Refer to the Javadoc for a detailed
 * explanation of their functionality. If the offered functionality is not sufficient for your needs
 * you can create your own constraints and use them in the generator configuration.
 * 
 */
public interface Constraint {

	/**
	 * Returns the value for this constraint.
	 * 
	 * @param c
	 *            the parameter c can be used to generate particular cases
	 * @return the value generate just now
	 */
	String initValues(FieldCase c);

	/**
	 * Returns the maximum number of characters this constraint can generate. For some constraints
	 * that return fixed values or the like this method will return -1
	 * 
	 * @return the maximum number of generated characters or -1 if it is unknown
	 */
	int getMaxLength();

	/**
	 * Resets all values contained in this constraint
	 */
	void resetValues();

	/**
	 * Returns the constraint's {@link FieldControl}-object.
	 */
	FieldControl getControl();

	/**
	 * Returns the number of this constraint's mandatory test cases. The number is dependent of the
	 * constraint's type, its respective sub-constraints and the chosen control types.
	 * 
	 * @return the number of this constraint's cases
	 */
	int countCases();

	/**
	 * Returns true, if this constraint still contains at least one mandatory case or if there will
	 * be mandatory cases because of the sub-constraints
	 * 
	 * @return true, if this constraint still contains an mandatory case, if not false
	 */
	boolean hasNextCase();

	/**
	 * Sets the cases to null for this constraint and its sub-constraints. The control object can be
	 * accessed directly if only this constraint shall be reset
	 */
	void resetCase();

	/**
	 * Sets a fixed value for this constraint. No more values will be generated if the fixed value
	 * is not null.
	 * 
	 * @param callback
	 *            this is a reference so that a potential fixed value can be retrieved at generate
	 *            time
	 */
	void setValueCallback(ValueCallback callback);

	/**
	 * Returns the id of this constraint if there is one. Null in the usual case.
	 * 
	 * @return the id of this constraint if there is one. Null in the usual case.
	 */
	String getId();

	/**
	 * Returns the id of this constraint or - if null - the id of the id of the next higher
	 * constrait that is not {@code null}. This ID should actually never be null and so can be used
	 * to determine for example the method which belongs to a given constraint.
	 */
	String getLastIdInHierarchy();

	/**
	 * Returns a set of all ids within this constraint. If this constraint contain no
	 * sub-constraints, the set only contains this constraint's id.
	 * 
	 * @return a list if all ids contained in this constraint
	 */
	Set<String> getContainedIds();
}
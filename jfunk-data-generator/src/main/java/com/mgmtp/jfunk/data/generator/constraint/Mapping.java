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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.base.BaseConstraint;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * Constraint that uses the values of a key-constraint as key to identify the constraints stored
 * with this key in its map whose values are then stored in the value table of of this mapping
 * constraint. Example:
 * 
 * <pre>
 * {@code
 * <constraint id="c1" class="com.mgmtp.jfunk.data.generator.constraint.Mapping">
 *   <key_constraint>
 *     <constraint_ref id="c2"/>
 *   </key_constraint>
 *   <entry>
 *     <key>0</key>
 *     <constraint>
 *       <field>
 *         <value>A</value>
 *       </field>
 *     </constraint>
 *   </entry>
 *   <default_entry>
 *     <constraint>
 *       <field>
 *         <value>B</value>
 *       </field>
 *     </constraint>
 *   </default_entry>
 * </constraint>
 * }
 * </pre>
 * 
 * Depending on the value of c2 the value of c1 is set: if c2 == 0, c1 is set to A. In all other
 * cases c1 is set to B.
 * 
 */
public class Mapping extends BaseConstraint {

	protected Map<String, Constraint> map;
	protected Constraint selectedConstraint;
	protected Constraint keyConstraint;
	public static final String DEFAULT_KEY = "@__default__@";

	public Mapping(final MathRandom random, final Element element, final Generator g) {
		super(random, element, g);
		@SuppressWarnings("unchecked")
		List<Element> entries = element.getChildren(XMLTags.ENTRY);
		map = new HashMap<String, Constraint>(entries.size());
		for (Element el : entries) {
			@SuppressWarnings("unchecked")
			List<Element> keys = el.getChildren(XMLTags.KEY);
			for (Element key : keys) {
				map.put(key.getText(), getConstraint(el));
			}
		}
		keyConstraint = getConstraint(element.getChild(XMLTags.KEY_CONSTRAINT));
		Element defaultEntryElement = element.getChild(XMLTags.DEFAULT_ENTRY);
		if (defaultEntryElement != null) {
			Constraint defaultEntry = getConstraint(element.getChild(XMLTags.DEFAULT_ENTRY));
			map.put(DEFAULT_KEY, defaultEntry);
		}
	}

	/**
	 * @return the maximum length of all embedded constraints
	 */
	@Override
	public int getMaxLength() {
		int max = -1;
		for (Constraint c : map.values()) {
			int cM = c.getMaxLength();
			if (max < cM) {
				max = cM;
			}
		}
		return max;
	}

	/**
	 * This method first calls {@link #initValues(FieldCase)} on the KeyConstraint and then
	 * determines the matching sub-constraint. If there is one it will be subsequently initialized.
	 * 
	 */
	@Override
	protected String initValuesImpl(final FieldCase ca) {
		String keyValue = keyConstraint.initValues(ca);
		selectedConstraint = map.get(keyValue);
		if (selectedConstraint == null) {
			selectedConstraint = map.get(DEFAULT_KEY);
		}
		if (selectedConstraint != null) {
			return selectedConstraint.initValues(ca);
		}
		return null;
	}

	/**
	 * Empties the value table and calls {@link #resetValues()} for each constraint in the table.
	 */
	@Override
	public void resetValues() {
		for (Constraint constraint : map.values()) {
			constraint.resetValues();
		}
	}

	/**
	 * Returns the maximum value that the method {@link #countCases()} takes for all sub-constraints
	 * in the table.
	 */
	@Override
	public int countCases() {
		int counter = 1;
		for (Constraint c : map.values()) {
			counter = Math.max(counter, c.countCases());
		}
		return counter;
	}

	/**
	 * Returns {@code true}, if at least one of the mapped constraints fulfills
	 * {@code hasNextCase() == true} .
	 */
	@Override
	public boolean hasNextCase() {
		if (selectedConstraint != null) {
			return selectedConstraint.hasNextCase();
		}
		return false;
	}

	/**
	 * Calls {@link Constraint#resetCase()} on all constraints in the table.
	 */
	@Override
	public void resetCase() {
		for (Constraint c : map.values()) {
			c.resetCase();
		}
	}

	/**
	 * Returns the set union of all id set of the contained constraints plus its own.
	 */
	@Override
	public Set<String> getContainedIds() {
		Set<String> s = super.getContainedIds();
		for (Constraint c : map.values()) {
			s.addAll(c.getContainedIds());
		}
		return s;
	}
}
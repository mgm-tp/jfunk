/*
 * Copyright (c) 2013 mgm technology partners GmbH
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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jdom.Element;

import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.base.BaseConstraint;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.FormatFactory;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * Sums the values of all embedded constraints. Single summands can be marked negative so that they
 * are then subtracted.
 * <p>
 * Example:
 * 
 * <pre>
 * {@code
 * <constraint id="c0" class="com.mgmtp.jfunk.data.generator.constraint.Sum">
 *   <summand>
 *     <constraint_ref id="c1"/>
 *   </summand>
 *   <summand negative="true">
 *     <constraint_ref id="c2"/>
 *   </summand>
 * </constraint>
 * <constraint id="c1">
 *   <field_ref id="integer"/>
 * </constraint>
 * <constraint id="c2">
 *   <field_ref id="integer"/>
 * </constraint>
 * }
 * </pre>
 * 
 * Given the example above, c0 would be calculated as c1 - c2.
 * 
 */
public class Sum extends BaseConstraint {

	private final NumberFormat format;
	private final Collection<ConstraintWrap> summands;

	public Sum(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
		format = FormatFactory.getNumberFormat(element);
		summands = new ArrayList<ConstraintWrap>();
		@SuppressWarnings("unchecked")
		List<Element> constraints = element.getChildren(XMLTags.SUMMAND);
		for (Element element2 : constraints) {
			summands.add(new ConstraintWrap(element2));
		}
	}

	/**
	 * @return -1
	 */
	@Override
	public int getMaxLength() {
		return -1;
	}

	/**
	 * does nothing
	 */
	@Override
	public void resetValues() {
		// nothing to do here
	}

	/**
	 * Calls {@link #initValues(FieldCase)} for all summands first. Then the sum of all values of
	 * every sub-constraint is taken whereupon the numbers are parsed using the format. Finally the
	 * value is returned formatted as a string using the format object.
	 * 
	 * @return the sum as string
	 */
	@Override
	protected String initValuesImpl(final FieldCase ca) {
		double summe = 0;
		boolean found = false;
		for (ConstraintWrap constraint : summands) {
			Double d = constraint.getSum(ca);
			if (d != null) {
				found = true;
				summe += d;
			}
		}
		// a string is only returned if there is at least one value found, otherwise Null
		if (found) {
			return format.format(summe);
		}
		return null;
	}

	/**
	 * Returns the value 1.
	 * 
	 * @return always 1
	 */
	@Override
	public int countCases() {
		return 1;
	}

	/**
	 * Returns true if one of the sub-constraints still contains mandatory cases
	 * 
	 * @return true if at least on of the sub-constraints contains at least one mandatory case
	 */
	@Override
	public boolean hasNextCase() {
		for (ConstraintWrap wrap : summands) {
			if (wrap.constraint.hasNextCase()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Calls resetCase for all summands
	 */
	@Override
	public void resetCase() {
		for (ConstraintWrap wrap : summands) {
			wrap.constraint.resetCase();
		}
	}

	/**
	 * Helper class that in each case embeds a summand constraint. The functionality of this helper
	 * class mainly resides in the fact that there is a method {@link #getSum(FieldCase)} which
	 * returns the sum of all values of the embedded constraints. Furthermore the value will be
	 * returned as negative if the negative flag is set.
	 */
	class ConstraintWrap {
		private final boolean negative;
		private final Constraint constraint;

		public ConstraintWrap(final Element element) {
			negative = JFunkConstants.TRUE.equals(element.getAttributeValue(XMLTags.NEGATIVE));
			constraint = getConstraint(element);
		}

		/**
		 * Calls resetValues on the embedded constraint
		 */
		public void resetValues() {
			constraint.resetValues();
		}

		/**
		 * Parses every value of the embedded constraint to a numerical value using the number
		 * format and adds them all. Depending on the negative flag the value will be multiplied by
		 * -1 before it is returned.
		 * 
		 * @return the sum of the embedded constraint
		 */
		Double getSum(final FieldCase c) {
			String s = constraint.initValues(c);
			if (s != null && s.length() > 0) {
				Number i;
				try {
					i = format.parse(s);
				} catch (ParseException e) {
					log.debug("Could not parse string " + s + " ; setting value to 0");
					return null;
				}
				return negative ? -1.0 * i.doubleValue() : i.doubleValue();
			}
			return null;
		}
	}
}
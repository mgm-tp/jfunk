/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.constraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.base.BaseConstraint;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.data.FormData;
import com.mgmtp.jfunk.data.generator.util.ValueCallback;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * The complex constraint combines several single constraints to one entity. The subunits can be
 * configured dependently so that the generate generates every combination of values or
 * independently so that no combination will be taken into account.
 * <p>
 * Example:
 * 
 * <pre>
 *  {@code
 *  <constraint class="com.mgmtp.jfunk.data.generator.constraint.Complex" dependent="true">
 *    <constraint>
 *      <field>
 *        <value>A</value>
 *        <value>B</value>
 *      </field>
 *    </constraint>
 *    <constraint>
 *      <field>
 *        <value>1</value>
 *        <value>2</value>
 *      </field>
 *    </constraint>
 * </constraint>
 *  }
 * </pre>
 * 
 * <p>
 * If {@code dependent} is set to {@code true} all possible combinations are generated at first: A1,
 * A2, B1, B2. If {@code dependent} is set to {@code false} the subconstraints are handled
 * individually and it will take more runs to generate all possible combinations.
 * </p>
 * If a complex constraint contains many subconstraints which have many different mandatory cases it
 * may make sense to set {@code dependent} to {@code false} as otherwise it may take a lot of runs
 * to generate all necessary cases, a purely random strategy may be more effective.
 * 
 * @version $Id$
 */
public class Complex extends BaseConstraint {

	protected List<Constraint> constraints;
	protected boolean dependent;

	public Complex(final MathRandom random, final Element el, final Generator generator) {
		super(random, el, generator);
		dependent = "true".equals(el.getAttributeValue(XMLTags.DEPENDENT));
		constraints = new ArrayList<Constraint>();
		ConstraintFactory factory = generator.getConstraintFactory();
		List<Element> children = el.getChildren();
		for (Element child : children) {
			if (XMLTags.CONSTRAINT.equals(child.getName())) {
				constraints.add(factory.createModel(random, child));
			} else if (XMLTags.CONSTRAINT_REF.equals(child.getName())) {
				constraints.add(factory.getModel(child));
			}
		}
	}

	/**
	 * Returns the maximum of all embedded constraints.
	 * 
	 * @return the maximum of all embedded constraints
	 */
	@Override
	public int getMaxLength() {
		int max = -1;
		for (Constraint c : constraints) {
			int cM = c.getMaxLength();
			if (max < cM) {
				max = cM;
			}
		}
		return max;
	}

	/**
	 * Does nothing
	 */
	@Override
	public void setValueCallback(final ValueCallback value) {
		// nothing
	}

	/**
	 * Initializes all subconstraints.
	 * 
	 * @return always {@code null}
	 */
	@Override
	public String initValues(final FieldCase ca) {
		if (ca != null) {
			/*
			 * If a FieldCase is given all fields will be generated anew, independent of the case
			 * combination.
			 */
			for (Constraint c : constraints) {
				c.resetValues();
			}
		}
		for (Constraint c : constraints) {
			c.initValues(ca);
		}
		return null;
	}

	@Override
	protected String initValuesImpl(final FieldCase c) throws Exception {
		// does nothing as this is only called by initValues in the BaseConstraint class
		throw new UnsupportedOperationException("Method initValuesImpl must not be called");
	}

	/**
	 * Resets the values of the sub-constraints. To achieve this the list of sub-constraints is
	 * passed backwards. If all sub-constraints are independent of each other, simply all
	 * sub-constraints will be reset. However, if they are dependent, the case of all constraints in
	 * the list below the first constraint which still has to process a test case will be reset and
	 * the run will be canceled. This way all the cases of the last sub-constraints will be run
	 * first. Then the next case will be called in the penultimate sub-constraint. Again all cases
	 * of the last one will be run, etc.
	 */
	@Override
	public void resetValues() {
		int length = constraints.size();
		for (int i = length - 1; i >= 0; i--) {
			Constraint c = constraints.get(i);
			c.resetValues();
			if (dependent && c.hasNextCase()) {
				// reset all constraints below
				// only if the constraint above still has a case to be processed
				for (int j = i + 1; j < length; j++) {
					constraints.get(j).resetCase();
				}
				break;
			}
		}
	}

	/**
	 * If the control object is set to {@code dependent} all sub-fields will be combined
	 * arbitrarily, i.e. all case numbers will be multiplied with each other. If the control fiel is
	 * not set to {@code dependent} only the maximum is taken as all other cases are covered by it.
	 * 
	 * @return the number of cases in this constraint
	 */
	@Override
	public int countCases() {
		int counter = 1;
		if (dependent) {
			for (Constraint constraint : constraints) {
				if (dependent) {
					counter *= constraint.countCases();
				} else {
					// TODO point can not be reached as dependent as dependent has been queried above
					// already.
					counter = Math.max(counter, constraint.countCases());
				}
			}
		}
		return counter;
	}

	/**
	 * Returns {@code true}, if at least on of the sub-constraints still has to process a case i.e.
	 * there is at least one sub-constraint with {@code hasNextCase() ==
	 * true}.
	 * 
	 * @return true, if one of the sub-constrains still has cases
	 */
	@Override
	public boolean hasNextCase() {
		// this method only returns true if dependent values are grouped
		if (!dependent) {
			return false;
		}
		for (Constraint c : constraints) {
			if (c.hasNextCase()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Calls {@link #resetCase()} for all sub-constraints.
	 */
	@Override
	public void resetCase() {
		for (Constraint c : constraints) {
			c.resetCase();
		}
	}

	/**
	 * Returns all ids contained in this complex {@link Constraint}. This method is used by the
	 * {@link FormData} to match all {@link Constraint Constraints}.
	 * 
	 * @return a set with all ids contained in this constraint
	 */
	@Override
	public Set<String> getContainedIds() {
		Set<String> s = super.getContainedIds();
		for (Constraint c : constraints) {
			s.addAll(c.getContainedIds());
		}
		return s;
	}
}
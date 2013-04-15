/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.constraint;

import java.util.Set;

import org.jdom.Element;

import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.base.BaseStateConstraint;
import com.mgmtp.jfunk.data.generator.control.BooleanControl;
import com.mgmtp.jfunk.data.generator.control.FieldCase;

/**
 * The field below is marked as optional meaning that the embedded constraint generates a value one
 * time and at another time does not. The mandatory cases will however be processed preferred, i.e.
 * only if {@link #hasNextCase()} has returned {@code false} will the value be optional. The
 * property {@link JFunkConstants#IGNORE_CONSTRAINT_OPTIONAL} is used to control globally whether
 * values can become optional at all or if they have to be generated always.
 * <p>
 * Example:
 * 
 * <pre>
 * {@code
 * <constraint class="com.mgmtp.jfunk.data.generator.constraint.Optional">
 *   <constraint class="com.mgmtp.jfunk.data.generator.constraint.RangeConstraint">
 *     <min>1</min>
 *     <max>10</max>
 *     <field_ref id="text"/>
 *   </constraint>
 * </constraint>
 * }
 * </pre>
 * 
 * In the given example first all the mandatory cases are processed and then no value will be
 * generated anymore for half of the cases. By the way, the following constraint can be used to
 * achieve a similar effect:
 * 
 * <pre>
 * {@code
 * <constraint class="com.mgmtp.jfunk.data.generator.constraint.RangeConstraint">
 *   <min>0</min>
 *   <max>10</max>
 *   <field_ref id="text"/>
 * </constraint>
 * }
 * </pre>
 * 
 * However, in this case the option to globally deactivate and to treat mandatory cases preferred
 * would be missing - so generally the first variant is recommended.
 * 
 */
public class Optional extends BaseStateConstraint {

	private boolean last;
	private final Constraint constraint;
	private final BooleanControl choice;

	public Optional(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
		choice = new BooleanControl(random);
		constraint = getConstraint(element);
		last = false;
	}

	/**
	 * @return the maxlength of the constraints
	 */
	@Override
	public int getMaxLength() {
		return constraint.getMaxLength();
	}

	/**
	 * Returns either null or a value. If null is returned the embedded constraint is initialized
	 * with FieldCase.NULL. If a value is returned the embedded constraint is initialized with the
	 * FieldCase whose value is returned. If the latest value was not not null the next value will
	 * be generated if in the case that the embedded constraint still has an mandatory case. This
	 * also applies if all optional constraints have been turned of globally using the property
	 * IGNORE_CONSTRAINT_OPTIONAL. Otherwise no value will be generated in 50% of the cases or the
	 * values of the embedded constraints are returned.
	 * 
	 * @return the value just generated
	 */
	@Override
	public String initValuesImpl(final FieldCase ca) {
		if (ca == FieldCase.NULL || ca == FieldCase.BLANK) {
			return null;
		}
		// If there still is an mandatory case, a value will be generated anyway
		if (ca != null || last && constraint.hasNextCase() || generator.isIgnoreOptionalConstraints()) {
			return constraint.initValues(ca);
		}
		if (choice.isNext()) {
			last = true;
			return constraint.initValues(null);
		}
		// Initialize the field below with 0 as optional equals false
		last = false;
		constraint.initValues(FieldCase.NULL);
		return null;
	}

	/**
	 * Empties the value table and calls the embedded constraint resetValues()
	 * 
	 * @see #resetValues()
	 */
	@Override
	public void resetValuesImpl() {
		constraint.resetValues();
	}

	/**
	 * Returns 1 + Number of cases of the embedded constraint
	 */
	@Override
	public int countCases() {
		// For the cardinal numbers only the cases with true will be taken into account
		// for false there is only exactly one case.
		return 1 + constraint.countCases();
	}

	/**
	 * Calls {@link BooleanControl#hasNext()} for the internal {@link BooleanControl}. If this
	 * returns {@code false}, {@link Constraint#hasNextCase()} from the embedded constraint is
	 * called.
	 */
	@Override
	public boolean hasNextCase() {
		if (choice.hasNext()) {
			return true;
		}
		return constraint.hasNextCase();
	}

	/**
	 * Sets the control object of this constraint and of the sub-constraint to null
	 */
	@Override
	public void resetCase() {
		choice.reset();
		constraint.resetCase();
	}

	@Override
	public Set<String> getContainedIds() {
		Set<String> s = super.getContainedIds();
		s.addAll(constraint.getContainedIds());
		return s;
	}
}

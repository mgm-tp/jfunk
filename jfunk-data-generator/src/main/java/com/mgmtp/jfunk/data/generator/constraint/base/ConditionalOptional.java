package com.mgmtp.jfunk.data.generator.constraint.base;

import java.util.Set;

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.Constraint;
import com.mgmtp.jfunk.data.generator.control.BooleanControl;
import com.mgmtp.jfunk.data.generator.control.FieldCase;

/**
 * Basic Class for fields that are mandatory fields in certain conditions and optional in others.
 * The conditions for the mandatory field are implemented in the method isMandatory. If this method
 * returns {@code true} the field is an mandatory field and the value is generated. If the method
 * returns {@code false} the field value is generated for half of the cases, and not generated for
 * the other half.
 * 
 * @version $Id$
 */
public abstract class ConditionalOptional extends BaseStateConstraint {

	private final Constraint constraint;
	private final BooleanControl choice;

	public ConditionalOptional(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
		constraint = getConstraint(element);
		choice = new BooleanControl(random);
	}

	/**
	 * @return the value of {@link #getMaxLength()} of the embedded constraint
	 */
	@Override
	public int getMaxLength() {
		return constraint.getMaxLength();
	}

	/**
	 * The implementation of this method returns {@code true} if the field is an mandatory field,
	 * {@code false} if it is an optional field. For this optional field in half of the cases a
	 * value will be generated, in the other half of cases not.
	 * 
	 * @return {@code true}, if the field is an mandatory field
	 */
	protected abstract boolean isMandatory();

	/**
	 * If the passed FieldCase equals {@link FieldCase#NULL}, {@code null} is returned. If
	 * {@link #isMandatory()} is {@code true} the field value is generated depending on the
	 * Fieldcase. If the {@link #isMandatory()} is {@code false} the constraint value is generated
	 * for only half the cases.
	 */
	@Override
	protected String initValuesImpl(final FieldCase c) throws Exception {
		if (c == FieldCase.NULL) {
			return null;
		}
		if (c != null || isMandatory() || choice.isNext()) {
			return constraint.initValues(c);
		}
		return constraint.initValues(FieldCase.NULL);
	}

	/**
	 * Reset the values
	 */
	@Override
	protected void resetValuesImpl() {
		constraint.resetValues();
	}

	/**
	 * Returns the constraint cases 2 times (once for mandatory and once for not mandatory) plus 1
	 * (for the optional case where no value is generated)
	 */
	@Override
	public int countCases() {
		return 2 * constraint.countCases() + 1;
	}

	/**
	 * If it is a mandatory field (@see #isMandatory) {@link #hasNextCase()} is returned. If not
	 * {@code true} is returned only if at least one of the optional cases (value or no value) has
	 * not yet occured.
	 */
	@Override
	public boolean hasNextCase() {
		if (isMandatory()) {
			return constraint.hasNextCase();
		}
		return choice.hasNext();
	}

	/**
	 * Resets the case counter and calls {@link #resetCase()} for the embedded constraint
	 */
	@Override
	public void resetCase() {
		choice.reset();
		constraint.resetCase();
	}

	/**
	 * Returns the set of constained ids. This Set contains all ids of the embedded constraint plus
	 * its own if this is set.
	 */
	@Override
	public Set<String> getContainedIds() {
		Set<String> s = super.getContainedIds();
		s.addAll(constraint.getContainedIds());
		return s;
	}
}
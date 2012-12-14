/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.constraint;

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.base.BaseStateConstraint;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.control.FieldControl;
import com.mgmtp.jfunk.data.generator.field.Field;

/**
 * Simple constraint containting a {@link Field}
 * 
 * @version $Id$
 */
public class FieldContainer extends BaseStateConstraint {

	protected Field field;
	protected FieldControl control;

	public FieldContainer(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
		field = generator.getFieldFactory().createField(random, element, characterSetId);
		control = generator.getControlFactory().getControl(random, element, field.getRange());
	}

	@Override
	public int getMaxLength() {
		return field.getMaxLength();
	}

	/**
	 * Returns a string whose length and character type reflect the passed FieldCase using the
	 * embedded field object. If FieldCase.NULL or FieldCase.BLANK is passed, the method returns
	 * null.
	 * 
	 * @return the value just generated
	 */
	@Override
	protected String initValuesImpl(final FieldCase ca) {
		if (ca == FieldCase.NULL || ca == FieldCase.BLANK) {
			return null;
		}
		return field.getString(control.getNext(ca));
	}

	/**
	 * Does nothing; the {@link Field} object does not have a state
	 */
	@Override
	protected void resetValuesImpl() {
		// nothing to do here
	}

	/**
	 * Returns the number of cases that the control object retains for the field
	 * 
	 * @return the number of mandatory cases in the control object
	 */
	@Override
	public int countCases() {
		return control.countCases();
	}

	/**
	 * Return the value of the method hasNext() of the FieldControl object
	 * 
	 * @return true, if the FieldControl still retains an mandatory case
	 * @see com.mgmtp.jfunk.data.generator.control.FieldControl#hasNext()
	 */
	@Override
	public boolean hasNextCase() {
		return control.hasNext();
	}

	/**
	 * Resets the FieldControl object
	 * 
	 * @see com.mgmtp.jfunk.data.generator.control.FieldControl#reset()
	 */
	@Override
	public void resetCase() {
		control.reset();
	}

	public Field getField() {
		return this.field;
	}
}
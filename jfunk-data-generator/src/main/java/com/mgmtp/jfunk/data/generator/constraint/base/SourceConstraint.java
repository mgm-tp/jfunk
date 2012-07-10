package com.mgmtp.jfunk.data.generator.constraint.base;

import java.util.Set;

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.Constraint;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * @version $Id$
 */
public abstract class SourceConstraint extends BaseConstraint {

	protected Constraint source;
	/**
	 * This variable is {@code true}, if the constraint was embedded directly (i.e. per child
	 * element {@code constraint}) and {@code false} if the Constraint was embedded using a
	 * reference (i.e. per child element {@code constraint_ref}). In this case the embedded
	 * constraint will not be reset when {@link #resetValues()} is called.
	 */
	private final boolean sourceEmbedded;

	public SourceConstraint(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
		sourceEmbedded = element.getChild(XMLTags.CONSTRAINT) != null;
		source = getConstraint(element);
	}

	@Override
	protected String initValuesImpl(final FieldCase c) throws Exception {
		return source.initValues(c);
	}

	/**
	 * Return MaxLength of the source constraint
	 * 
	 * @return MaxLength of the source constraint
	 */
	@Override
	public int getMaxLength() {
		return source.getMaxLength();
	}

	/**
	 * Calls {@link #resetValues()} for the embedded constraint, but only if the constraint was
	 * embeeded directly (see {@link #sourceEmbedded}).
	 */
	@Override
	public void resetValues() {
		if (sourceEmbedded) {
			source.resetValues();
		}
	}

	@Override
	public int countCases() {
		return source.countCases();
	}

	@Override
	public boolean hasNextCase() {
		return source.hasNextCase();
	}

	/**
	 * Calls {@link #resetCase()} for the embedded constraint but only if the constraint was
	 * embeeded directly (see {@link #sourceEmbedded}).
	 */
	@Override
	public void resetCase() {
		if (sourceEmbedded) {
			source.resetCase();
		}
	}

	/**
	 * @return the list of the ids of the constraint above including its own id
	 */
	@Override
	public Set<String> getContainedIds() {
		Set<String> s = super.getContainedIds();
		s.addAll(source.getContainedIds());
		return s;
	}
}
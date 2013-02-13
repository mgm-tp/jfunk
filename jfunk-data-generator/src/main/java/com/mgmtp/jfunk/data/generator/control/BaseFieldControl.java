/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.control;

import java.util.Collections;
import java.util.List;

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;

/**
 * Basic class for the implementation of the FieldControl interface. The basic function is already
 * implemented here and the configuration for shuffle (all cases are mixed before execution) or bad
 * (if true, forbidden characters are generated in the field values) are built in already as well.
 * 
 * @version $Id$
 */
public abstract class BaseFieldControl implements FieldControl {

	protected Range range;
	private List<FieldCase> caseStack;
	private boolean allHit;
	private final boolean shuffle;
	protected final MathRandom random;

	protected BaseFieldControl(final MathRandom random, final Element el, final Range range) {
		this.random = random;
		shuffle = "true".equals(el.getAttributeValue("shuffle"));
		this.range = range;
	}

	/**
	 * Extended setter for the CaseStack. If the CaseStach is null it will be generated using the
	 * method to be implemented {@link #createCases()}. If shuffle is true as well, the generated
	 * list will also be shuffled. This list is then returned. If the list is not null, just the
	 * existing list will be returned.
	 * 
	 * @return a list of all {@link FieldCase} objects for this {@link FieldControl}
	 */
	protected final List<FieldCase> getCaseStack() {
		if (caseStack == null) {
			allHit = false;
		}
		if (caseStack == null || caseStack.isEmpty()) {
			caseStack = createCases();
			if (shuffle) {
				Collections.shuffle(caseStack);
			}
		}
		return caseStack;
	}

	/**
	 * The Implementation of this method takes care of the generation of the different cases.
	 * 
	 * @return the initial generated list of all FieldCases.
	 */
	protected abstract List<FieldCase> createCases();

	@Override
	public final Range getRange() {
		return range;
	}

	/**
	 * If the {@link FieldCase} passed is {@code null}, the next case in the list of mandatory cases
	 * is returned. If the {@link FieldCase} passed is not {@code null}, a case will be generated
	 * dependent of it.
	 * 
	 * @return the next case in the application flow
	 */
	@Override
	public final FieldCase getNext(FieldCase ca) {
		if (ca == null) {
			List<FieldCase> stack = getCaseStack();
			ca = stack.remove(0);
			allHit = allHit | stack.isEmpty();
		} else {
			ca = createCase(ca);
		}
		return ca;
	}

	protected FieldCase createCase(final FieldCase ca) {
		return ca.getInstance(this);
	}

	@Override
	public final void reset() {
		allHit = false;
		caseStack = null;
	}

	@Override
	public final boolean hasNext() {
		return !allHit;
	}
}
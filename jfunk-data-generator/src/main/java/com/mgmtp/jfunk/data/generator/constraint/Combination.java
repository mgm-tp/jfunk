/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.constraint;

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.ValueCallback;

/**
 * The combined constraint contains several sub-constraints and returns as value the character
 * string which contains all character string of the sub-constraints.
 * <p>
 * Example:
 * 
 * <pre>
 *  {@code
 *  <constraint id="combined_constraint" class="com.mgmtp.jfunk.data.generator.constraint.Combination">
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
 * This would result in either {@code A1}, {@code B1}, {@code A2} or {@code B2}.
 * 
 * @version $Id$
 */
public class Combination extends Complex {

	private final StringBuffer buffer = new StringBuffer();

	public Combination(final MathRandom random, final Element el, final Generator generator) {
		super(random, el, generator);
	}

	/**
	 * Puts a fixed value for this combination of values
	 */
	@Override
	public void setValueCallback(final ValueCallback value) {
		valueCallback = value;
	}

	/**
	 * Returns the sum of all maximum lengths of the sub-constraints
	 * 
	 * @return the sum of all maximum lengths of the sub-constraints
	 */
	@Override
	public int getMaxLength() {
		int max = 0;
		boolean found = false;
		for (Constraint c : constraints) {
			int cM = c.getMaxLength();
			if (cM != -1) {
				max += cM;
				found = true;
			}
		}
		return found ? max : -1;
	}

	/**
	 * Initializes all sub-constraints. Returns a character string composed of all character strings
	 * of the constraints contained in it. If all sub-constraints return null, null is returned.
	 * 
	 */
	@Override
	public String initValues(final FieldCase ca) {
		boolean found = false;
		buffer.setLength(0);
		for (Constraint c : constraints) {
			String cV = c.initValues(ca);
			if (cV != null) {
				found = true;
				buffer.append(cV);
			}
		}
		if (found) {
			return buffer.toString();
		}
		return null;
	}
}
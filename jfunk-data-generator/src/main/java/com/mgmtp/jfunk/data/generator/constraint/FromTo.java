/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.constraint;

import java.util.Collections;
import java.util.List;

import org.jdom.Element;

import com.google.common.collect.Lists;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.control.FieldCase;

/**
 * This complex constraint generates values for its sub-constraints so that they are in ascending
 * order. After 10000 unsuccessful tries an exception is thrown.
 * <p>
 * Example:
 * 
 * <pre>
 * {@code 
 * <constraint id="c0" class="com.mgmtp.jfunk.data.generator.constraint.FromTo">
 *   <constraint id="c1">
 *     <field>
 *       <value>A</value>
 *       <value>B</value>
 *       <value>C</value>
 *     </field>
 *   </constraint>
 *   <constraint id="c2">
 *     <field>
 *       <value>A</value>
 *       <value>B</value>
 *       <value>C</value>
 *     </field>
 *   </constraint>
 * </constraint>
 * }
 * </pre>
 * 
 * The constraints c1 and c2 are not generated individually anymore but considered as a unit such
 * that c1 combined with c2 always is "ascending". Generated values would be:
 * <table>
 * <tr>
 * <th>c1</th>
 * <td>A</td>
 * <td>A</td>
 * <td>A</td>
 * <td>B</td>
 * <td>B</td>
 * <td>C</td>
 * </tr>
 * <tr>
 * <th>c2</th>
 * <td>A</td>
 * <td>B</td>
 * <td>C</td>
 * <td>B</td>
 * <td>C</td>
 * <td>C</td>
 * </tr>
 * </table>
 * Please note that constraint c0 does not have a value, {@link #initValues(FieldCase)} always
 * returns {@code null}.
 * 
 * @version $Id$
 */
public class FromTo extends Complex {
	public FromTo(final MathRandom random, final Element el, final Generator generator) {
		super(random, el, generator);
	}

	/**
	 * @return always null - only affects the values of the sub-constraints.
	 */
	@Override
	public String initValues(final FieldCase c) {
		List<String> lastValues = Lists.newArrayListWithCapacity(constraints.size());
		List<String> values = Lists.newArrayListWithCapacity(constraints.size());
		List<String> sortedValues = Lists.newArrayListWithCapacity(constraints.size());
		int generate = 10000;
		int equalsCount = 0;
		while (generate > 0) {
			super.initValues(c);
			for (Constraint constraint : constraints) {
				String s = constraint.initValues(c);
				if (s != null) {
					values.add(s);
				}
			}
			if (values.isEmpty()) {
				break;
			}
			sortedValues.addAll(values);
			Collections.sort(sortedValues);
			if (values.equals(sortedValues)) {
				break;
			}
			if (values.equals(lastValues)) {
				equalsCount++;
			} else {
				equalsCount = 0;
			}
			if (equalsCount == 5) {
				log.debug("Generated values were equal in a row for five times und thus can be assumed as fixed");
				break;
			}
			lastValues.clear();
			lastValues.addAll(values);
			values.clear();
			sortedValues.clear();
			resetValues();
			generate--;
		}
		if (generate == 0) {
			throw new IllegalStateException("Could not generate values in ascending order after 10000 tries");
		}
		return null;
	}
}
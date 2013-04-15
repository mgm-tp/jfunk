/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.control;

import java.util.List;

import org.jdom.Element;

import com.google.common.collect.Lists;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * Freely configurable FieldControl Implementation. The separate cases are directly specified in the
 * XML configuation via the case child element. There are different types:
 * <ul>
 * <li>"min": The smallest allowed value</li>
 * <li>"avg": The value lies in the middle of the allowed range</li>
 * <li>"max": The biggest allowes value</li>
 * <li>"null": Null value is initialized</li>
 * <li>"blank": Depending on the field a string with a certain length containing spaces is generated
 * </li>
 * <li>"lt_min": Value smaller than allowed</li>
 * <li>"gt_max": Value bigger than allowed</li>
 * </ul>
 * 
 */
public class EnumerationControl extends BaseFieldControl {

	private final List<FieldCase> cases = Lists.newArrayList();

	public EnumerationControl(final MathRandom random, final Element el, final Range range) {
		super(random, el, range);

		@SuppressWarnings("unchecked")
		List<Element> caseElements = el.getChildren(XMLTags.CASE);
		for (Element caseElement : caseElements) {
			String type = caseElement.getAttributeValue(XMLTags.TYPE);
			String typeU = type.toUpperCase();
			try {
				cases.add((FieldCase) FieldCase.class.getField(typeU).get(null));
			} catch (NoSuchFieldException ex) {
				throw new IllegalArgumentException("The FieldCase type " + type
						+ " does not exist as a constant in the class FieldCase and so is not allowed", ex);
			} catch (IllegalAccessException ex) {
				throw new IllegalStateException("Error accessing the FieldCase constant " + typeU, ex);
			}
		}
	}

	@Override
	protected List<FieldCase> createCases() {
		List<FieldCase> fieldCases = Lists.newArrayListWithExpectedSize(cases.size());
		for (FieldCase fieldCase : cases) {
			fieldCases.add(createCase(fieldCase));
		}
		return fieldCases;
	}

	@Override
	public int countCases() {
		return cases.size();
	}
}
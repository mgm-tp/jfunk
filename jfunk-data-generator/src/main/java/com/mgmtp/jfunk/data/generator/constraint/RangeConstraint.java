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

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * A special {@link FieldContainer} with a minimal and a maximal length.
 * 
 */
public class RangeConstraint extends FieldContainer {
	public RangeConstraint(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
		Range range = field.getRange();
		String minS = element.getChildTextTrim(XMLTags.MIN);
		String maxS = element.getChildTextTrim(XMLTags.MAX);
		int min = minS.length() == 0 ? range.getMin() : Integer.parseInt(minS);
		int max = maxS.length() == 0 ? range.getMax() : Integer.parseInt(maxS);
		Range r = new Range(min, max);
		field.setRange(r);
		control = generator.getControlFactory().getControl(random, element, r);
	}

	@Override
	protected String initValuesImpl(FieldCase ca) {
		ca = control.getNext(ca);
		return super.initValuesImpl(ca);
	}
}
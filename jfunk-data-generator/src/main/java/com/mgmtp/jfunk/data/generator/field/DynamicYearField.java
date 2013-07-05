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
package com.mgmtp.jfunk.data.generator.field;

import static com.google.common.base.Preconditions.checkState;

import java.util.Calendar;
import java.util.List;

import org.jdom.Element;

import com.google.common.collect.Lists;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * This field describes a date range in relation to the current calendar year. The elements
 * {@code min} and {@code max} in the XML configuration describe the minimum and maximum values,
 * respectively.
 * <p>
 * Example:
 * 
 * <pre>
 * {@code
 * <field class="com.mgmtp.jfunk.data.generator.field.DynamicYearField">
 *   <min>-3</min>
 *   <max>+2</max>
 * </field>
 * }
 * </pre>
 * 
 * If the current calender year is 2011, so the years 2008 to 2013 are generated.
 * 
 */
public final class DynamicYearField extends Field {

	private final List<String> values;
	private Range range;

	public DynamicYearField(final MathRandom random, final Element element, final String characterSetId) {
		super(random, element, characterSetId);

		String minString = element.getChildText(XMLTags.MIN);
		if (minString.startsWith("+")) {
			minString = minString.substring(1);
		}
		String maxString = element.getChildText(XMLTags.MAX);
		if (maxString.startsWith("+")) {
			maxString = maxString.substring(1);
		}

		int min = Integer.parseInt(minString);
		int max = Integer.parseInt(maxString);

		checkState(min <= max, "min (=" + min + ") must not exceed the max (=" + max + ") ");

		int year = Calendar.getInstance().get(Calendar.YEAR);
		int minYear = year + min;
		int maxYear = year + max;

		values = Lists.newArrayListWithExpectedSize(maxYear - minYear + 1);
		for (int i = minYear; i <= maxYear; ++i) {
			values.add(String.valueOf(i));
		}
		range = new Range(0, values.size() - 1);
	}

	@Override
	public Range getRange() {
		return range;
	}

	@Override
	public void setRange(final Range range) {
		this.range = range;
	}

	@Override
	public int getMaxLength() {
		return 4;
	}

	@Override
	public String getString(final FieldCase c) {
		int index = c.getSize();
		index = index < 0 ? 0 : index;
		return values.get(index % values.size());
	}
}
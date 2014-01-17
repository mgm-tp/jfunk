/*
 * Copyright (c) 2014 mgm technology partners GmbH
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

import java.util.List;

import org.jdom.Element;

import com.google.common.collect.Lists;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * This field type returns one value at a time from the list of its values as they are set down in
 * the XML configuration.
 * <p>
 * Example:
 * 
 * <pre>
 * {@code
 * <field class="com.mgmtp.jfunk.data.generator.field.Enumeration">
 *   <value>A</value>
 *   <value>B</value>
 *   <value>C</value>
 * </field>
 * }
 * </pre>
 * 
 * The attribute {@code class} is optional. A field without class attribute is a Enumeration by
 * default.
 * 
 */
public final class Enumeration extends Field {

	private final List<String> values;
	private Range range;

	public Enumeration(final MathRandom random, final Element element, final String characterSetId) {
		super(random, element, characterSetId);
		@SuppressWarnings("unchecked")
		List<Element> valueElements = element.getChildren(XMLTags.VALUE);
		values = Lists.newArrayListWithExpectedSize(valueElements.size());
		for (Element valueElement : valueElements) {
			String value = valueElement.getText();
			if (value != null) {
				values.add(value);
			}
		}
		range = new Range(0, values.size() - 1);
	}

	@Override
	public void setRange(final Range range) {
		this.range = range;
	}

	/**
	 * @return the length of the longest allowed value
	 */
	@Override
	public int getMaxLength() {
		int max = -1;
		for (String value : values) {
			int length = value.length();
			if (max < length) {
				max = length;
			}
		}
		return max;
	}

	/**
	 * Returns a randomly chosen value for the given possible values. For FieldCase.NULL
	 * {@code null} is returned. The value of the range object is the index of the element from the
	 * list whose value is returned. D.
	 * 
	 * @return the currently selected value
	 */
	@Override
	public String getString(final FieldCase c) {
		int index = c.getSize();
		// if it is to big than normalize to 0 (bad cases are ignored)
		index = index < 0 ? 0 : index;
		return values.get(index % values.size());
	}

	/**
	 * @return a range object [0,number of possible values - 1]
	 */
	@Override
	public Range getRange() {
		return range;
	}
}
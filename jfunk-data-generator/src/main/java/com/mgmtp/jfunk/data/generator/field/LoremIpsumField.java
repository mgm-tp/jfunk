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

import org.apache.commons.lang3.text.StrBuilder;
import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;
import com.mgmtp.jfunk.data.generator.config.LoremIpsum;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * 
 * @author rnaegele
 */
public final class LoremIpsumField extends Field {

	private Range range;

	@LoremIpsum
	String loremIpsum;

	public LoremIpsumField(final MathRandom random, final Element element, final String characterSetId) {
		super(random, element, characterSetId);
		int min = Integer.parseInt(element.getChildText(XMLTags.MIN));
		int max = Integer.parseInt(element.getChildText(XMLTags.MAX));
		range = new Range(min, max);
	}

	@Override
	public int getMaxLength() {
		return range.getMax();
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
	public String getString(final FieldCase c) {
		int size = c.getSize();

		if (size == FieldCase.JUST_ABOVE_MAX) {
			size++;
		} else if (size == FieldCase.JUST_BELOW_MIN) {
			size--;
		}

		if (size <= 0) {
			return "";
		}

		int loremLength = loremIpsum.length();
		if (loremLength < size) {
			int multiplier = size / loremLength;
			int remainder = size % loremLength;

			StrBuilder sb = new StrBuilder(multiplier * size + remainder);
			for (int i = 0; i < multiplier; ++i) {
				sb.append(loremIpsum);
			}
			sb.append(loremIpsum.substring(0, remainder));
			return sb.toString();
		}

		return loremIpsum.substring(0, size);
	}
}
/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
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
 * @version $Id: $
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
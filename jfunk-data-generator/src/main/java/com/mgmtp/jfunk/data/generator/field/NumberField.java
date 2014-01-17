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

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.regex.Pattern;

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.CharacterSet;
import com.mgmtp.jfunk.data.generator.util.FormatFactory;
import com.mgmtp.jfunk.data.generator.util.GeneratingExpression;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * This field implementation returns formatted numeric values. A minimum and a maximum value have to
 * be given. Optionally Null can be excluded from generation using the element with the value
 * {@link XMLTags#NOTZERO}
 * <p>
 * Example:
 * 
 * <pre>
 * {@code
 * <field class="com.mgmtp.jfunk.data.generator.field.NumberField">
 *   <control_ref id="linear.control"/>
 *   <min>-5</min>
 *   <max>5</max>
 *   <notzero>true</notzero>
 * </field>
 * }
 * </pre>
 * 
 * This would generate numbers from -5 to 5 skipping zero.
 * 
 */
public final class NumberField extends Field {

	private static final Pattern ZERO_PATTERN = Pattern.compile("-?0[,.]?0*");
	private double min;
	private double max;
	private boolean notZero;
	private final NumberFormat format;
	private GeneratingExpression expression;
	private int maxLength = -1;
	private Range range;

	public NumberField(final MathRandom random, final Element element, final String characterSetId) {
		super(random, element, characterSetId);
		format = FormatFactory.getNumberFormat(element);
		try {
			min = format.parse(element.getChildText(XMLTags.MIN)).doubleValue();
			max = format.parse(element.getChildText(XMLTags.MAX)).doubleValue();
		} catch (ParseException e) {
			throw new IllegalStateException("could not parse numbers", e);
		}
		Element notZeroElement = element.getChild(XMLTags.NOTZERO);
		if (notZeroElement != null) {
			notZero = XMLTags.TRUE.equals(notZeroElement.getText());
		}
		Element expElement = element.getChild(XMLTags.EXPRESSION);
		String exp = null;
		if (expElement == null) {
			exp = "[\\-\\s0-9,]+";
		} else {
			exp = expElement.getText();
		}
		try {
			expression = new GeneratingExpression(random, exp, CharacterSet.getCharacterSet(characterSetId));
		} catch (IOException e) {
			throw new IllegalStateException("Error while initialising String generator", e);
		}
		range = new Range(1, 5);
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
		if (maxLength == -1) {
			Range r = getRange();
			for (int i = r.getMin(); i <= r.getMax(); i++) {
				maxLength = Math.max(maxLength, getString(new FieldCase(i)).length());
			}
		}
		return maxLength;
	}

	/**
	 * If {@link FieldCase#isBad()} is {@code true}, an invalid number is generated using
	 * {@link GeneratingExpression#negateString(String, int)}
	 * 
	 * @return a character string representing a random number subject to the FieldCase
	 */
	@Override
	public String getString(final FieldCase c) {
		boolean success = false;
		String numberString;
		do {
			double number = getDouble(c);
			numberString = format.format(number);
			if (notZero && ZERO_PATTERN.matcher(numberString).matches()) {
				log.info(XMLTags.NOTZERO + " is true and a zero value was generated: value=" + numberString + " - generating new value");
			} else {
				success = true;
			}

		} while (!success);
		if (c.isBad()) {
			numberString = expression.negateString(numberString, c.getBad());
		}
		return numberString;
	}

	public NumberFormat getFormat() {
		return format;
	}

	private double getDouble(final FieldCase c) {
		int size = c.getSize();
		double number = 0.0;
		double rnd = random.getDouble(1);
		if (size == 3 && notZero) {
			// case 3 must not occur in a notzero field
			size = 4;
		}
		if (size == FieldCase.JUST_ABOVE_MAX) {
			number = max + 1;
		} else if (size == FieldCase.JUST_BELOW_MIN) {
			number = min - 1;
		} else if (size < 1) {
			number = min - rnd * (max - min);
		} else if (size > 5) {
			number = max + rnd * (max - min);
		} else if (size == 1) {
			number = min;
		} else if (size == 5) {
			number = max;
		} else if (size == 2 && min < 0 && max > 0) {
			number = min - rnd * min; // ]min,0[
		} else if (size == 3 && min < 0 && max > 0) {
			number = 0;
		} else if (size == 4 && min < 0 && max > 0) {
			number = rnd * max; // ]0,max[
		} else {
			number = min + rnd * (max - min);
		}
		return number;
	}
}
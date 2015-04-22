/*
 * Copyright (c) 2015 mgm technology partners GmbH
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.CharacterSet;
import com.mgmtp.jfunk.data.generator.util.GeneratingExpression;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * Implementation of {@link Field} which generates values matching a given regular expression.
 * <p>
 * Example:
 * 
 * <pre>
 * {@code
 * <field class="com.mgmtp.jfunk.data.generator.field.InverseExpression">
 *   <expression>[A-Z]</expression>
 * </field>
 * }
 * </pre>
 * 
 * This would generate upper case letters.
 * 
 */
public final class InverseExpression extends Field {

	private GeneratingExpression exp;
	private Range range;
	private final String expression2;

	public InverseExpression(final MathRandom random, final Element element, final String characterSetId) {
		super(random, element, characterSetId);
		String expression = StringUtils.trim(element.getChildText(XMLTags.EXPRESSION));
		// expression2 is optional
		expression2 = StringUtils.trim(element.getChildText(XMLTags.EXPRESSION + "2"));

		try {
			CharacterSet set = CharacterSet.getCharacterSet(characterSetId);
			exp = new GeneratingExpression(random, expression, set);
		} catch (IOException e) {
			throw new IllegalStateException("Error initialising GeneratingExpression", e);
		}
		range = exp.getRange();
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
		if (c == FieldCase.NULL) {
			return null;
		}
		int total = c.getSize();
		int bad = c.getBad();

		if (c.isNegative()) {
			switch (total) {
				case FieldCase.JUST_ABOVE_MAX:
					total = getMaxLength() + 1;
					break;
				case FieldCase.JUST_BELOW_MIN:
					total = range.getMin() - 1;
					if (total <= 0) {
						bad = 1;
						total = 1;
					}
					break;
				default:
					break;
			}
		}
		/*
		 * If expression2 is null, a string is generated based on the expression specified in the
		 * generator configuration. If expression2 is not null, the generated expression will be
		 * evaluated against expression2 and has to match. If it does not match, a new value will be
		 * generated.
		 */
		if (expression2 == null) {
			// HtmlUnit cannot handle multiple blanks correctly so we do not generate those
			return exp.generate(total, bad).replaceAll("\\s+", " ");
		}
		boolean matches;
		int counter = 1000;
		String result;
		do {
			// HtmlUnit cannot handle multiple blanks correctly so we do not generate those
			result = exp.generate(total, bad).replaceAll("\\s+", " ");
			Pattern p = Pattern.compile(expression2);
			Matcher m = p.matcher(result);
			matches = m.matches();
			if (counter-- == 0) {
				throw new IllegalArgumentException("Could not generate a valid value after 1000 tries. expression2=" + expression2
						+ " does never match a generated value. Last value generated=" + result);
			}
		} while (!matches);
		return result;
	}
}
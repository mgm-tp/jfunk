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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.base.SourceConstraint;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * This constraint takes the value of its embedded constraint and uses the regular expression of the
 * constant tag. The value of this one's first group will be returned as value. Using this
 * constraint parts of a string can be selected out of a source string and be processed as a value
 * of its own.
 * <p>
 * Example:
 * 
 * <pre>
 * {@code 
 * <constraint id="c1" class="com.mgmtp.jfunk.data.generator.constraint.SelectConstraint">
 *   <constant>\D+(\d+)</constant>
 *   <constraint id="c2">
 *     <field class="com.mgmtp.jfunk.data.generator.field.InverseExpression">
 *       <expression>[A-Z]+\d+</expression>
 *     </field>
 *   </constraint>
 * </constraint>
 * }
 * </pre>
 * 
 * In this example c1 would contain the numeric part from c2.
 * 
 */
public class SelectConstraint extends SourceConstraint {

	private final Pattern pattern;

	public SelectConstraint(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
		pattern = Pattern.compile(element.getChildText(XMLTags.CONSTANT));
	}

	@Override
	protected String initValuesImpl(final FieldCase c) throws Exception {
		String value = source.initValues(c);
		if (value == null || value.length() == 0) {
			return null;
		}
		Matcher matcher = pattern.matcher(value);
		if (!matcher.matches()) {
			throw new IllegalStateException("The Pattern " + pattern.pattern() + " does not match the source string " + value);
		}
		return matcher.group(1);
	}

	@Override
	public int getMaxLength() {
		return -1;
	}
}
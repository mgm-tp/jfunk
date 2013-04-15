/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.field;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.CharacterSet;
import com.mgmtp.jfunk.data.generator.util.FormatFactory;
import com.mgmtp.jfunk.data.generator.util.GeneratingExpression;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * A field generating date values within a range given by a minimum and maximum value. The format is
 * determined by the XML configuration. Upper and lower boundaries can also be left empty, in this
 * case the following applies:
 * <ul>
 * <li>if no lower boundary is set 01.01.1970 is used as value</li>
 * <li>if no upper boundary is set the current date is used as value</li>
 * </ul>
 * <p>
 * Example 1:
 * 
 * <pre>
 * {@code
 * <field class="com.mgmtp.jfunk.data.generator.field.DateField">
 *   <control_ref id="linear.control"/>
 *   <min>1.1.2005</min>
 *   <min>31.12.2010</min>
 * </field>
 * }
 * </pre>
 * 
 * This example uses the global data format in the XML-configuration.
 * <p>
 * Example 2:
 * 
 * <pre>
 * {@code
 * <field class="com.mgmtp.jfunk.data.generator.field.DateField">
 *   <format>
 *     <date>
 *       <pattern>yyyy</pattern>
 *       <locale>
 *         <language>de</language>
 *         <country>de</country>
 *       </locale>
 *     </date>
 *   </format>
 *   <min>2001</min>
 *   <max>2050</max>
 * </field>
 * }
 * </pre>
 * 
 * This example uses a different format to generate only year dates.
 * 
 */
public final class DateField extends Field {

	private GeneratingExpression expression;
	private final DateFormat format;
	private long min;
	private long max;
	private Range range;
	private int maxLength = -1;

	public DateField(final MathRandom random, final Element element, final String characterSetId) {
		super(random, element, characterSetId);
		format = FormatFactory.getDateFormat(element);
		String minString = element.getChildText(XMLTags.MIN);
		String maxString = element.getChildText(XMLTags.MAX);
		try {
			if (StringUtils.isNotBlank(minString)) {
				min = format.parse(minString).getTime();
			} else {
				min = 0;
			}
			if (StringUtils.isNotBlank(maxString)) {
				max = format.parse(maxString).getTime();
			} else {
				// if no maximum is set the current date is taken as the maximum
				max = System.currentTimeMillis();
			}
		} catch (ParseException e) {
			throw new IllegalStateException("could not parse dates", e);
		}
		Element expElement = element.getChild(XMLTags.EXPRESSION);
		String exp = null;
		if (expElement == null) {
			exp = "[0-9,.]{10}";
		} else {
			exp = expElement.getText();
		}
		try {
			expression = new GeneratingExpression(random, exp, CharacterSet.getCharacterSet(characterSetId));
		} catch (IOException e) {
			throw new IllegalStateException("Error while initialising String generator", e);
		}
		/*
		 * In this case the range controls only the boundaries and not the length. Range is 1 to 4
		 * for min, avg, avg, max so that on average double the average cases to the min and max
		 * cases are generated
		 */
		range = new Range(1, 4);
	}

	@Override
	public void setRange(final Range range) {
		this.range = range;
	}

	@Override
	public int getMaxLength() {
		if (maxLength == -1) {
			for (int i = range.getMin(); i <= range.getMax(); i++) {
				maxLength = Math.max(maxLength, getString(new FieldCase(i)).length());
			}
		}
		return maxLength;
	}

	/**
	 * Returns a formatted date string corresponding - depending on the field case- the earliest or
	 * latest allowed or an average date. {@code null} is returned for FieldCase.NULL. Apart from
	 * that a date object is generated that lies within the range or corresponds with the minimum or
	 * maximum. If {@link FieldCase#isBad()}) is {@code true} the generated date string is
	 * transformed into an invalid one using {@link GeneratingExpression#negateString(String, int)}.
	 * 
	 * @return a date string corresponding to the passed FieldCase
	 */
	@Override
	public String getString(final FieldCase c) {
		int size = c.getSize();
		long date = 0;

		if (size == FieldCase.JUST_BELOW_MIN) {
			date = min - 86400000; // - 1 Tag
		} else if (size == FieldCase.JUST_ABOVE_MAX) {
			date = max + 86400000; // + 1 Tag
		} else if (size < range.getMin()) {
			date = min - random.getLong(max - min);
		} else if (size > range.getMax()) {
			date = max + random.getLong(max - min);
		} else if (size == range.getMin()) {
			date = min;
		} else if (size == range.getMax()) {
			date = max;
		} else {
			date = min + random.getLong(max - min);
		}
		String dateString = format.format(new Date(date));
		if (c.isBad()) {
			dateString = expression.negateString(dateString, c.getBad());
		}
		return dateString;
	}

	/**
	 * Returns the range that in this case only controls the boundaries and not the length of the
	 * generated field.
	 * 
	 * @return the Range of this field object
	 */
	@Override
	public Range getRange() {
		return range;
	}
}
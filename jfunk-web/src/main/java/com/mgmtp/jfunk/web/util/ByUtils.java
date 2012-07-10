package com.mgmtp.jfunk.web.util;

import java.util.List;

import org.openqa.selenium.By;

/**
 * Utility method to provide commonly used Xpath expressions for use with {@link By#xpath(String)}.
 * 
 * @version $Id$
 */
public class ByUtils {

	private ByUtils() {
		// don't allow instantiation
	}

	public static By clickElementWithNameAndValue(final String name, final String value) {
		StringBuilder sb = new StringBuilder();
		sb.append("//input[@name='");
		sb.append(name);
		sb.append("' and @value='");
		sb.append(value);
		sb.append("']");
		return By.xpath(sb.toString());
	}

	public static By clickElementWithNameAndValue(final List<String> names, final List<String> values) {
		StringBuilder sb = new StringBuilder();
		sb.append("//input[");
		if (!names.isEmpty()) {
			if (names.size() > 1) {
				sb.append("(");
			}
			int count = 0;
			for (String name : names) {
				if (count > 0) {
					sb.append(" or ");
				}
				sb.append("@name='");
				sb.append(name);
				sb.append("'");
				count++;
			}
			if (names.size() > 1) {
				sb.append(")");
			}
		}
		if (!values.isEmpty()) {
			if (!names.isEmpty()) {
				sb.append(" and ");
			}
			if (values.size() > 1) {
				sb.append("(");
			}
			int count = 0;
			for (String value : values) {
				if (count > 0) {
					sb.append(" or ");
				}
				sb.append("@value='");
				sb.append(value);
				sb.append("'");
				count++;
			}
			if (values.size() > 1) {
				sb.append(")");
			}
		}
		sb.append("]");
		return By.xpath(sb.toString());
	}

	public static By clickElementWithNameAndValueAndId(final String name, final String value, final String id) {
		StringBuilder sb = new StringBuilder();
		sb.append("//input[@name='");
		sb.append(name);
		sb.append("' and @value='");
		sb.append(value);
		sb.append("' and @id='");
		sb.append(id);
		sb.append("']");
		return By.xpath(sb.toString());
	}

	public static By clickElementWithValue(final String value) {
		StringBuilder sb = new StringBuilder();
		sb.append("//input[@value='");
		sb.append(value);
		sb.append("']");
		return By.xpath(sb.toString());
	}

	public static By clickElementWithValueAndId(final String value, final String id) {
		StringBuilder sb = new StringBuilder();
		sb.append("//input[@value='");
		sb.append(value);
		sb.append("' and @id='");
		sb.append(id);
		sb.append("']");
		return By.xpath(sb.toString());
	}
}
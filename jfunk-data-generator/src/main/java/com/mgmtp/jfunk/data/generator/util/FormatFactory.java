package com.mgmtp.jfunk.data.generator.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.jdom.Element;

/**
 * Helper class that takes care of the generation of NumberFormat and DateFormat objects from given
 * element objects
 * 
 * @version $Id$
 */
public final class FormatFactory {

	private FormatFactory() {
		// don't allow instantiation
	}

	/**
	 * Searches for a format element in the given element. If none is found the XML tree hierarchy
	 * is searched upwards unto the root. If this is the case the default format object is returned.
	 */
	public static NumberFormat getNumberFormat(final Element element) {
		if (XMLTags.FORMAT.equals(element.getName())) {
			return createNumberFormat(element);
		}
		Element child = element.getChild(XMLTags.FORMAT);
		if (child != null) {
			return createNumberFormat(child);
		}
		return getNumberFormat(element.getParentElement());
	}

	/**
	 * Searches for a format element in the given element. If none is found the XML tree hierarchy
	 * is searched upwards unto the root. If this is the case the default format object is returned.
	 */
	public static DateFormat getDateFormat(final Element element) {
		if (XMLTags.FORMAT.equals(element.getName())) {
			return createDateFormat(element);
		}
		Element child = element.getChild(XMLTags.FORMAT);
		if (child != null) {
			return createDateFormat(child);
		}
		return getDateFormat(element.getParentElement());
	}

	private static NumberFormat createNumberFormat(final Element element) {
		Element numberFormatElement = element.getChild(XMLTags.NUMBER);
		String pattern = numberFormatElement.getChildText(XMLTags.PATTERN);
		Element localeElement = numberFormatElement.getChild(XMLTags.LOCALE);
		String language = localeElement.getChildText(XMLTags.LANGUAGE);
		String country = localeElement.getChildText(XMLTags.COUNTRY);
		Locale locale = new Locale(language, country);
		return new DecimalFormat(pattern, new DecimalFormatSymbols(locale));
	}

	private static DateFormat createDateFormat(final Element element) {
		Element numberFormatElement = element.getChild(XMLTags.DATE);
		String pattern = numberFormatElement.getChildText(XMLTags.PATTERN);
		Element localeElement = numberFormatElement.getChild(XMLTags.LOCALE);
		String language = localeElement.getChildText(XMLTags.LANGUAGE);
		String country = localeElement.getChildText(XMLTags.COUNTRY);
		Locale locale = new Locale(language, country);
		return new SimpleDateFormat(pattern, locale);
	}
}
package com.mgmtp.jfunk.web.util;

import java.util.regex.Pattern;

import org.openqa.selenium.WebElement;

/**
 * @author rnaegele
 * @version $Id: Predicates.java 28330 2012-10-09 13:16:34Z reinhard.naegele $
 */
public class WebElementPredicates {

	private WebElementPredicates() {
		// don't allow instantiation
	}

	public static BasePredicate<WebElement, Void> textMatchesPattern(final Pattern pattern) {
		return new TextMatchesPatternPredicate(pattern);
	}

	public static BasePredicate<WebElement, Void> textMatchesPattern(final String regex) {
		return new TextMatchesPatternPredicate(regex);
	}

	public static BasePredicate<WebElement, Void> attributeValueMatchesPattern(final Pattern pattern, final String attribute) {
		return new AttributeValueMatchesPatternPredicate(pattern, attribute);
	}

	public static BasePredicate<WebElement, Void> attributeValueMatchesPattern(final String regex, final String attribute) {
		return new AttributeValueMatchesPatternPredicate(regex, attribute);
	}

	private static class TextMatchesPatternPredicate extends BasePredicate<WebElement, Void> {

		private final Pattern pattern;

		public TextMatchesPatternPredicate(final Pattern pattern) {
			this.pattern = pattern;
		}

		public TextMatchesPatternPredicate(final String regex) {
			this(Pattern.compile(regex));
		}

		@Override
		public boolean doApply(final WebElement input) {
			return pattern.matcher(input.getText()).matches();
		}
	}

	private static class AttributeValueMatchesPatternPredicate extends BasePredicate<WebElement, Void> {

		private final Pattern pattern;
		private final String attribute;

		public AttributeValueMatchesPatternPredicate(final Pattern pattern, final String attribute) {
			this.pattern = pattern;
			this.attribute = attribute;
		}

		public AttributeValueMatchesPatternPredicate(final String regex, final String attribute) {
			this(Pattern.compile(regex), attribute);
		}

		@Override
		public boolean doApply(final WebElement input) {
			return pattern.matcher(input.getAttribute(attribute)).matches();
		}
	}
}

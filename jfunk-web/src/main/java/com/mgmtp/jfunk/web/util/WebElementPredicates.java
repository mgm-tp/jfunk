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

	public static BasePredicate<WebElement, Void> newTextMatchesPatternPredicate(final Pattern pattern) {
		return new TextMatchesPatternPredicate(pattern);
	}

	public static BasePredicate<WebElement, Void> newTextMatchesPatternPredicate(final String regex) {
		return new TextMatchesPatternPredicate(regex);
	}

	private static class TextMatchesPatternPredicate extends BasePredicate<WebElement, Void> {

		private final Pattern pattern;

		public TextMatchesPatternPredicate(final Pattern pattern) {
			this.pattern = pattern;
		}

		public TextMatchesPatternPredicate(final String regex) {
			this.pattern = Pattern.compile(regex);
		}

		@Override
		public boolean doApply(final WebElement input) {
			return pattern.matcher(input.getText()).matches();
		}
	}
}

package com.mgmtp.jfunk.web.util;

import java.util.regex.Pattern;

import org.openqa.selenium.WebElement;

import com.google.common.base.Predicate;

/**
 * @author rnaegele
 * @version $Id: Predicates.java 28330 2012-10-09 13:16:34Z reinhard.naegele $
 */
public class WebElementPredicates {

	private WebElementPredicates() {
		// don't allow instantiation
	}

	public static Predicate<WebElement> newTextMatchesPatternPredicate(final Pattern pattern) {
		return new TextMatchesPatternPredicate(pattern);
	}

	public static Predicate<WebElement> newTextMatchesPatternPredicate(final String regex) {
		return new TextMatchesPatternPredicate(regex);
	}

	private static class TextMatchesPatternPredicate extends BasePredicate<WebElement> {

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

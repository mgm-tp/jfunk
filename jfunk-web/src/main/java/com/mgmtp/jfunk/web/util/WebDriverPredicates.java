package com.mgmtp.jfunk.web.util;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

/**
 * @author rnaegele
 * @version $Id: Predicates.java 28330 2012-10-09 13:16:34Z reinhard.naegele $
 */
public class WebDriverPredicates {

	private static final Logger LOG = LoggerFactory.getLogger(WebDriverPredicates.class);

	private WebDriverPredicates() {
		// don't allow instantiation
	}

	public static Predicate<WebDriver> urlMatchesPattern(final Pattern pattern) {
		return new UrlMatchesPatternPredicate(pattern);
	}

	public static Predicate<WebDriver> urlMatchesPattern(final String regex) {
		return new UrlMatchesPatternPredicate(regex);
	}

	public static Predicate<WebDriver> pageSourceContainsString(final String searchString) {
		return new PageSourceContainsStringPredicate(searchString, false, true);
	}

	public static Predicate<WebDriver> pageSourceContainsString(final String searchString, final boolean caseSensitive) {
		return new PageSourceContainsStringPredicate(searchString, caseSensitive, true);
	}

	public static Predicate<WebDriver> pageSourceContainsString(final String searchString, final boolean caseSensitive, final boolean mustExist) {
		return new PageSourceContainsStringPredicate(searchString, caseSensitive, mustExist);
	}

	public static Predicate<WebDriver> pageSourceMatchesPattern(final String pattern, final boolean mustExist) {
		return new PageSourceMatchesPatternPredicate(pattern, mustExist);
	}

	public static Predicate<WebDriver> pageSourceMatchesPattern(final Pattern pattern, final boolean mustExist) {
		return new PageSourceMatchesPatternPredicate(pattern, mustExist);
	}

	private static class UrlMatchesPatternPredicate extends BasePredicate<WebDriver> {

		private final Pattern pattern;

		public UrlMatchesPatternPredicate(final Pattern pattern) {
			this.pattern = pattern;
		}

		public UrlMatchesPatternPredicate(final String regex) {
			this.pattern = Pattern.compile(regex);
		}

		@Override
		public boolean doApply(final WebDriver input) {
			return pattern.matcher(input.getCurrentUrl()).matches();
		}
	}

	private static class PageSourceContainsStringPredicate extends BasePredicate<WebDriver> {

		private final String searchString;
		private final boolean caseSensitive;
		private final boolean mustExist;

		public PageSourceContainsStringPredicate(final String searchString, final boolean caseSensitive, final boolean mustExist) {
			this.searchString = searchString;
			this.caseSensitive = caseSensitive;
			this.mustExist = mustExist;
		}

		@Override
		public boolean doApply(final WebDriver input) {
			String pageSource = input.getPageSource().replaceAll("\\s+", " ");
			log.trace("pageSource={}", pageSource);

			boolean outcome = caseSensitive ? pageSource.contains(searchString) : containsIgnoreCase(pageSource, searchString);
			return mustExist ? outcome : !outcome;
		}
	}

	private static class PageSourceMatchesPatternPredicate extends BasePredicate<WebDriver> {

		private final Pattern pattern;
		private final boolean mustExist;

		public PageSourceMatchesPatternPredicate(final String pattern, final boolean mustExist) {
			this(Pattern.compile(pattern), mustExist);
		}

		public PageSourceMatchesPatternPredicate(final Pattern pattern, final boolean mustExist) {
			this.pattern = pattern;
			this.mustExist = mustExist;
		}

		@Override
		public boolean doApply(final WebDriver input) {
			String pageSource = input.getPageSource().replaceAll("\\s+", " ");
			log.trace("pageSource={}", pageSource);

			boolean outcome = pattern.matcher(pageSource).matches();
			return mustExist ? outcome : !outcome;
		}
	}
}

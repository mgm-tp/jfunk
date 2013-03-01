/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.util;

import static com.google.common.collect.Lists.newArrayListWithCapacity;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 * @author rnaegele
 * @version $Id: Functions.java 28330 2012-10-09 13:16:34Z reinhard.naegele $
 */
public class WebDriverFunctions {

	private WebDriverFunctions() {
		// don't allow instantiation
	}

	public static Function<WebDriver, List<String>> pageSourceMatchesPattern(final String pattern, final boolean mustExist) {
		return new PageSourceMatchesPatternFunction(pattern, mustExist);
	}

	public static Function<WebDriver, List<String>> pageSourceMatchesPattern(final Pattern pattern, final boolean mustExist) {
		return new PageSourceMatchesPatternFunction(pattern, mustExist);
	}

	private static class PageSourceMatchesPatternFunction implements Function<WebDriver, List<String>> {

		private final Pattern pattern;
		private final boolean mustExist;

		public PageSourceMatchesPatternFunction(final String pattern, final boolean mustExist) {
			this(Pattern.compile(pattern), mustExist);
		}

		public PageSourceMatchesPatternFunction(final Pattern pattern, final boolean mustExist) {
			this.pattern = pattern;
			this.mustExist = mustExist;
		}

		@Override
		public List<String> apply(final WebDriver input) {
			String pageSource = input.getPageSource().replaceAll("\\s+", " ");

			Matcher matcher = pattern.matcher(pageSource);
			boolean outcome = mustExist && matcher.matches();

			List<String> groupValues = null;
			if (mustExist && outcome) {
				int groupCount = matcher.groupCount();
				if (groupCount > 0) {
					groupValues = newArrayListWithCapacity(groupCount);

					for (int i = 1; i <= groupCount; ++i) {
						String groupValue = matcher.group(i);
						groupValues.add(groupValue);
					}
				} else {
					groupValues = ImmutableList.of();
				}
			}

			// return null in case of failure!!!
			return outcome ? groupValues : null;
		}

		@Override
		public String toString() {
			return String.format("page %sto match pattern '%s'", mustExist ? "" : "not ", pattern);
		}
	}
}

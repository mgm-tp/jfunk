/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.util;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 * {@link Function}s that may be used e. g. in {@link WebDriverTool#waitFor(Function)} similarly to
 * {@link ExpectedConditions}.
 * 
 * @author rnaegele
 */
public class WebDriverFunctions {

	private WebDriverFunctions() {
		// don't allow instantiation
	}

	public static Function<WebDriver, List<String>> pageSourceMatchesPattern(final String pattern) {
		return new PageSourceMatchesPatternFunction(Pattern.compile(pattern));
	}

	public static Function<WebDriver, List<String>> pageSourceMatchesPattern(final Pattern pattern) {
		return new PageSourceMatchesPatternFunction(pattern);
	}

	private static class PageSourceMatchesPatternFunction implements Function<WebDriver, List<String>> {

		private final Pattern pattern;

		public PageSourceMatchesPatternFunction(final Pattern pattern) {
			this.pattern = pattern;
		}

		@Override
		public List<String> apply(final WebDriver input) {
			String pageSource = normalizeSpace(input.getPageSource());

			Matcher matcher = pattern.matcher(pageSource);

			List<String> groupValues = null;
			if (matcher.matches()) {
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

				return groupValues;
			}

			// return null in case of failure!!!
			return null;
		}

		@Override
		public String toString() {
			return String.format("page to match pattern '%s'", pattern);
		}
	}
}

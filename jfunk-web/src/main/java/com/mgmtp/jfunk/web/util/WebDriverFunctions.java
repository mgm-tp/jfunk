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
package com.mgmtp.jfunk.web.util;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public static <V> Function<WebDriver, V> refreshOnFalseNullOrException(final Function<WebDriver, V> delegate) {
		return new RefreshOnFalseNullOrExceptionWrapperFunction<V>(delegate);
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

	private static class RefreshOnFalseNullOrExceptionWrapperFunction<V> implements Function<WebDriver, V> {
		private final Logger log = LoggerFactory.getLogger(getClass());

		private final Function<WebDriver, V> delegate;

		public RefreshOnFalseNullOrExceptionWrapperFunction(final Function<WebDriver, V> delegate) {
			this.delegate = delegate;
		}

		@Override
		public V apply(final WebDriver input) {
			boolean needsRefresh = true;
			try {
				V result = delegate.apply(input);
				needsRefresh = result instanceof Boolean && !(Boolean) result || result == null;
				return result;
			} finally {
				if (needsRefresh) {
					log.trace("Refreshing page...");
					input.navigate().refresh();
				}
			}
		}

		@Override
		public String toString() {
			return delegate.toString();
		}
	}

}

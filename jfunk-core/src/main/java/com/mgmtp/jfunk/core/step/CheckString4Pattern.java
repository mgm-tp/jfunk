/*
 * Copyright (c) 2015 mgm technology partners GmbH
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
package com.mgmtp.jfunk.core.step;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.exception.PatternException;
import com.mgmtp.jfunk.core.exception.StepException;
import com.mgmtp.jfunk.core.step.base.BaseStep;

/**
 * Searches for a regular expression in the given source string. Can throw an exception if the
 * regular expression matches or does not match. The content of the first capturing group (if
 * present) can be stored in the configuration.
 * 
 */
public class CheckString4Pattern extends BaseStep {
	private final Pattern pattern;
	private final boolean mustExist;
	private final String groupKey;
	private String groupValue;
	private final String source;

	@Inject
	Configuration config;

	public CheckString4Pattern(final String sourceString, final String regex, final String mustExist) {
		this(sourceString, regex, !JFunkConstants.FALSE.equals(mustExist));
	}

	public CheckString4Pattern(final String source, final String pattern) {
		this(source, pattern, true);
	}

	public CheckString4Pattern(final String sourceString, final String regex, final boolean mustExist) {
		this(sourceString, regex, null, mustExist);
	}

	/**
	 * @param sourceString
	 *            the string to match the regex against
	 * @param regex
	 *            the regex
	 * @param groupKey
	 *            if non-null, the content of the first capturing (if present) group is stored in
	 *            the configuration und this key
	 * @param mustExist
	 *            if {@code true} the regex must match, if {@code false} it must not match
	 */
	public CheckString4Pattern(final String sourceString, final String regex, final String groupKey, final boolean mustExist) {
		source = sourceString;
		pattern = Pattern.compile(regex);
		this.groupKey = groupKey;
		this.mustExist = mustExist;
	}

	public String getGroupValue() {
		return groupValue;
	}

	@Override
	public void execute() throws StepException {
		log.info("Regular expression {} must {}match in string {}", pattern, mustExist ? "" : "not ", source);

		Matcher matcher = pattern.matcher(source);
		boolean match = matcher.matches();
		if (match && matcher.groupCount() > 0) {
			// grouping stuff auswerten
			groupValue = matcher.group(1);
			if (groupKey != null && groupKey.length() > 0) {
				log.info("Setting property {} to {}", groupKey, groupValue);
				config.put(groupKey, groupValue);
			}
		}
		if (mustExist != match) {
			throw new PatternException("String", pattern, mustExist);
		}
	}
}
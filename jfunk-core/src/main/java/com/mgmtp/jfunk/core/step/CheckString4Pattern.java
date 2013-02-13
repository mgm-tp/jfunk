/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.step;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.exception.PatternException;
import com.mgmtp.jfunk.core.exception.StepException;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.core.step.base.BaseStep;

/**
 * Searches for a regular expression in the given source string. Can throw an exception if the
 * regular expression matches or does not match. The content of the first capturing group (if
 * present) can be stored in the configuration.
 * 
 * @version $Id$
 */
public class CheckString4Pattern extends BaseStep {
	private final Pattern pattern;
	private final boolean mustExist;
	private final String groupKey;
	private String groupValue;
	private final String source;

	@Inject
	Configuration config;

	@Deprecated
	public CheckString4Pattern(final String sourceString, final String regex, final String mustExist, final TestModule test) {
		this(sourceString, regex, !JFunkConstants.FALSE.equals(mustExist));
	}

	public CheckString4Pattern(final String sourceString, final String regex, final String mustExist) {
		this(sourceString, regex, !JFunkConstants.FALSE.equals(mustExist));
	}

	@Deprecated
	public CheckString4Pattern(final String source, final String pattern, @SuppressWarnings("unused") final TestModule test) {
		this(source, pattern, true);
	}

	public CheckString4Pattern(final String source, final String pattern) {
		this(source, pattern, true);
	}

	@Deprecated
	public CheckString4Pattern(final String sourceString, final String regex, final boolean mustExist,
			@SuppressWarnings("unused") final TestModule test) {
		this(sourceString, regex, null, mustExist);
	}

	public CheckString4Pattern(final String sourceString, final String regex, final boolean mustExist) {
		this(sourceString, regex, null, mustExist);
	}

	@Deprecated
	public CheckString4Pattern(final String sourceString, final String regex, final String groupKey, final boolean mustExist, final TestModule test) {
		this(sourceString, regex, groupKey, mustExist);
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
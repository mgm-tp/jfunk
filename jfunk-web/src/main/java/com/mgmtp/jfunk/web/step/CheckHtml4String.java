/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.step;

import static com.mgmtp.jfunk.common.util.Varargs.va;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import com.mgmtp.jfunk.core.exception.ValidationException;
import com.mgmtp.jfunk.core.module.TestModule;

/**
 * Searches for a string on the current HTML page.
 * 
 * @version $Id$
 */
public class CheckHtml4String extends WebDriverStep {
	private final String string;
	private final boolean mustExist;
	private final boolean caseSensitive;

	/*
	 * @see #CheckHtml4String(String)
	 */
	@Deprecated
	public CheckHtml4String(final String string, @SuppressWarnings("unused") final TestModule test) {
		this(string, true, true);
	}

	/**
	 * @see #CheckHtml4String(String, boolean)
	 */
	@Deprecated
	public CheckHtml4String(final String string, final boolean caseSensitive, @SuppressWarnings("unused") final TestModule test) {
		this(string, caseSensitive, true);
	}

	/**
	 * @see #CheckHtml4String(String, boolean, boolean)
	 */
	@Deprecated
	public CheckHtml4String(final String string, final boolean caseSensitive, final boolean mustExist,
			@SuppressWarnings("unused") final TestModule test) {
		this(string, caseSensitive, mustExist);
	}

	/**
	 * Creates a new instance. Search is case-sensitive. The string is expected to be found.
	 * 
	 * @param string
	 *            the string to be searched for
	 */
	public CheckHtml4String(final String string) {
		this(string, true, true);
	}

	/**
	 * Creates a new instance. The string is expected to be found.
	 * 
	 * @param string
	 *            the string to be searched for
	 * @param caseSensitive
	 *            {@code true} if search is to be case-sensitive
	 */
	public CheckHtml4String(final String string, final boolean caseSensitive) {
		this(string, caseSensitive, true);
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param string
	 *            the string to be searched for
	 * @param caseSensitive
	 *            {@code true} if search is to be case-sensitive
	 * @param mustExist
	 *            if {@code true}, the string must be found, otherwise it must not be found
	 */
	public CheckHtml4String(final String string, final boolean caseSensitive, final boolean mustExist) {
		this.string = string;
		this.caseSensitive = caseSensitive;
		this.mustExist = mustExist;
	}

	@Override
	public void execute() {
		log.info("String '{}' must {}exist in the page source. Search is {}case-sensitive.",
				va(string, mustExist ? "" : "not ", caseSensitive ? "" : "not "));

		String pageSource = getWebDriver().getPageSource();
		boolean outcome = caseSensitive ? pageSource.contains(string) : containsIgnoreCase(pageSource, string);

		if (mustExist != outcome) {
			if (mustExist) {
				throw new ValidationException("Could not find string '" + string + "' (case-sensitive=" + caseSensitive + ")");
			}
			throw new ValidationException("String '" + string + "' must not occur");
		}
	}
}
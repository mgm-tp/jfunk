/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.exception;

import java.util.regex.Pattern;


/**
 * This exception is thrown when an error occurred during pattern matching e.g. searching for
 * strings in HTML pages.
 * 
 * @version $Id$
 */
public class PatternException extends ValidationException {
	public PatternException(final String location, final Pattern pattern, final boolean mustExist) {
		super(createMessage(location, pattern.pattern(), mustExist));
	}

	public PatternException(final String location, final String pattern, final boolean mustExist) {
		super(createMessage(location, pattern, mustExist));
	}

	private static String createMessage(final String location, final String pattern, final boolean mustExist) {
		if (mustExist) {
			return "Regex pattern " + pattern + " did not match in " + location;
		}
		return "Regex pattern " + pattern + " must not match in " + location;
	}
}
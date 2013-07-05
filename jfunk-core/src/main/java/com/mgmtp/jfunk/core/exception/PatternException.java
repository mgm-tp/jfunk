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
package com.mgmtp.jfunk.core.exception;

import java.util.regex.Pattern;


/**
 * This exception is thrown when an error occurred during pattern matching e.g. searching for
 * strings in HTML pages.
 * 
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
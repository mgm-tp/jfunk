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
package com.mgmtp.jfunk.common.cli;

/**
 * Exception throw when a command-line execution fails.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
public class CliException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CliException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CliException(final String message) {
		super(message);
	}

	public CliException(final Throwable cause) {
		super(cause);
	}
}

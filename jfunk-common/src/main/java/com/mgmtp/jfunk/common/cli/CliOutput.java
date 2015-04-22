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
package com.mgmtp.jfunk.common.cli;

/**
 * <p>
 * Represents command line output.
 * </p>
 * <p>
 * Wraps standard output, error output, and exit code of a process.
 * </p>
 * 
 * @author rnaegele
 * @author sherold
 * @since 3.1.0
 */
public final class CliOutput {

	private final String stdOut;
	private final String stdErr;
	private final int exitCode;

	CliOutput(final String stdOut, final String stdErr, final int exitCode) {
		this.stdOut = stdOut;
		this.stdErr = stdErr;
		this.exitCode = exitCode;
	}

	/**
	 * @return the standard output
	 */
	public String getStdOut() {
		return stdOut;
	}

	/**
	 * @return the error output
	 */
	public String getStdErr() {
		return stdErr;
	}

	/**
	 * @return the exit code
	 */
	public int getExitCode() {
		return exitCode;
	}
}

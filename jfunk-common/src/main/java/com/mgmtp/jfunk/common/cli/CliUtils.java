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

import java.io.InputStream;

import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for executing command lines.
 * 
 * @author rnaegele
 * @author sherold
 * @since 3.1.0
 */
public class CliUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(CliUtils.class);

	/**
	 * Executes the specified command line and blocks until the process has finished. The output of
	 * the process is captured, returned, as well as logged with info (stdout) and error (stderr)
	 * level, respectively.
	 * 
	 * @param cli
	 *            the command line
	 * @return the process' output
	 */
	public static CliOutput executeCommandLine(final Commandline cli) {
		return executeCommandLine(cli, null);
	}

	/**
	 * Executes the specified command line and blocks until the process has finished. The output of
	 * the process is captured, returned, as well as logged with info (stdout) and error (stderr)
	 * level, respectively.
	 * 
	 * @param cli
	 *            the command line
	 * @param loggerName
	 *            the name of the logger to use (passed to {@link LoggerFactory#getLogger(String)});
	 *            if {@code null} this class' name is used
	 * @return the process' output
	 */
	public static CliOutput executeCommandLine(final Commandline cli, final String loggerName) {
		return executeCommandLine(cli, loggerName, null);
	}

	/**
	 * Executes the specified command line and blocks until the process has finished. The output of
	 * the process is captured, returned, as well as logged with info (stdout) and error (stderr)
	 * level, respectively.
	 * 
	 * @param cli
	 *            the command line
	 * @param loggerName
	 *            the name of the logger to use (passed to {@link LoggerFactory#getLogger(String)});
	 *            if {@code null} this class' name is used
	 * @param logMessagePrefix
	 *            if non-{@code null} consumed lines are prefix with this string
	 * @return the process' output
	 */
	public static CliOutput executeCommandLine(final Commandline cli, final String loggerName, final String logMessagePrefix) {
		try {
			String cliString = CommandLineUtils.toString(cli.getShellCommandline());
			LOGGER.info("Executing command-line: {}", cliString);

			LoggingStreamConsumer out = new LoggingStreamConsumer(loggerName, logMessagePrefix, false);
			LoggingStreamConsumer err = new LoggingStreamConsumer(loggerName, logMessagePrefix, true);
			int exitCode = CommandLineUtils.executeCommandLine(cli, out, err);
			return new CliOutput(out.getOutput(), err.getOutput(), exitCode);
		} catch (CommandLineException ex) {
			throw new CliException("Error executing command-line process.", ex);
		}
	}

	/**
	 * Executes the specified command line and blocks until the process has finished or till the
	 * timeout is reached. The output of the process is captured, returned, as well as logged with
	 * info (stdout) and error (stderr) level, respectively.
	 * 
	 * @param cli
	 *            the command line
	 * @param inputStream
	 *            the process input to read from, must be thread safe
	 * @param timeoutInSeconds
	 *            a positive integer to specify timeout, zero and negative integers for no timeout
	 * 
	 * @return the process' output
	 */
	public static CliOutput executeCommandLine(final Commandline cli, final InputStream inputStream, final int timeoutInSeconds) {
		return executeCommandLine(cli, null, inputStream, timeoutInSeconds);
	}

	/**
	 * Executes the specified command line and blocks until the process has finished or till the
	 * timeout is reached. The output of the process is captured, returned, as well as logged with
	 * info (stdout) and error (stderr) level, respectively.
	 * 
	 * @param cli
	 *            the command line
	 * @param loggerName
	 *            the name of the logger to use (passed to {@link LoggerFactory#getLogger(String)});
	 *            if {@code null} this class' name is used
	 * @param inputStream
	 *            the process input to read from, must be thread safe
	 * @param timeoutInSeconds
	 *            a positive integer to specify timeout, zero and negative integers for no timeout
	 * 
	 * @return the process' output
	 */
	public static CliOutput executeCommandLine(final Commandline cli, final String loggerName, final InputStream inputStream,
			final int timeoutInSeconds) {
		return executeCommandLine(cli, loggerName, null, inputStream, timeoutInSeconds);
	}

	/**
	 * Executes the specified command line and blocks until the process has finished or till the
	 * timeout is reached. The output of the process is captured, returned, as well as logged with
	 * info (stdout) and error (stderr) level, respectively.
	 * 
	 * @param cli
	 *            the command line
	 * @param loggerName
	 *            the name of the logger to use (passed to {@link LoggerFactory#getLogger(String)});
	 *            if {@code null} this class' name is used
	 * @param logMessagePrefix
	 *            if non-{@code null} consumed lines are prefix with this string
	 * @param inputStream
	 *            the process input to read from, must be thread safe
	 * @param timeoutInSeconds
	 *            a positive integer to specify timeout, zero and negative integers for no timeout
	 * 
	 * @return the process' output
	 */
	public static CliOutput executeCommandLine(final Commandline cli, final String loggerName, final String logMessagePrefix,
			final InputStream inputStream, final int timeoutInSeconds) {
		try {
			String cliString = CommandLineUtils.toString(cli.getShellCommandline());
			LOGGER.info("Executing command-line: {}", cliString);

			LoggingStreamConsumer stdOut = new LoggingStreamConsumer(loggerName, logMessagePrefix, false);
			LoggingStreamConsumer stdErr = new LoggingStreamConsumer(loggerName, logMessagePrefix, true);

			int exitCode = CommandLineUtils.executeCommandLine(cli, inputStream, stdOut, stdErr, timeoutInSeconds);
			return new CliOutput(stdOut.getOutput(), stdErr.getOutput(), exitCode);
		} catch (CommandLineException ex) {
			throw new CliException("Error executing command-line process.", ex);
		}
	}

	/**
	 * Throws a {@link CliException} if the specified exit code is non-zero.
	 * 
	 * @param exitCode
	 *            the exit code
	 * @throws CliException
	 *             if the specified exit code is non-zero
	 */
	public static void checkExitCode(final int exitCode) {
		LOGGER.info("Exit code of CLI execution: " + exitCode);
		if (exitCode != 0) {
			throw new CliException("Process finished with exit code " + exitCode);
		}
	}
}

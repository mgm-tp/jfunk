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

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link StreamConsumer} for capturing and logging consumed lines.
 * 
 * @author rnaegele
 * @author sherold
 * @since 3.1.0
 */
@ThreadSafe
public final class LoggingStreamConsumer implements StreamConsumer {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private final Logger logger;
	private final boolean error;
	private final String logMessagePrefix;

	// StringBuffer is used for thread safety!
	private final StringBuffer sb = new StringBuffer(200);

	/**
	 * @param loggerName
	 *            the name of the logger to use (passed to {@link LoggerFactory#getLogger(String)});
	 *            if {@code null} this class' name is used
	 * @param logMessagePrefix
	 *            if non-{@code null} consumed lines are prefix with this string
	 * @param error
	 *            specifies whether info or error level is used for logging
	 */
	public LoggingStreamConsumer(final String loggerName, final String logMessagePrefix, final boolean error) {
		this.logger = LoggerFactory.getLogger(StringUtils.defaultString(loggerName, getClass().getName()));
		this.logMessagePrefix = logMessagePrefix;
		this.error = error;
	}

	/**
	 * {@inheritDoc} Adds each line to an internal {@link StringBuffer} whose contents may be
	 * retrieved in {@link #getOutput()} and logs each line applying a non-null
	 * {@code logMessagePrefix} using error or info level depending on the {@code error} parameter
	 * passed in the constructor.
	 */
	@Override
	public void consumeLine(final String line) {
		sb.append(line).append(LINE_SEPARATOR);

		String logMessage = logMessagePrefix != null ? logMessagePrefix + line : line;
		if (error) {
			logger.error(logMessage);
		} else {
			logger.info(logMessage);
		}
	}

	/**
	 * Returns the contents of the internal {@link StringBuffer}. Does not reset the
	 * {@link StringBuffer}.
	 * 
	 * @return the output as a string
	 */
	public String getOutput() {
		return sb.toString();
	}
}

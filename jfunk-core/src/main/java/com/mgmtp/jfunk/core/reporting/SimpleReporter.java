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
package com.mgmtp.jfunk.core.reporting;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.nio.charset.Charset;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.core.step.base.Step;

/**
 * <p>
 * Creates a semi-colon-delimited report containing information on reported {@link TestModule}s and {@link Step}s. Report columns
 * are quoted.
 * </p>
 * <p>
 * The report contains the following columns:
 * <ol>
 * <li>An ISO timestamp specifying when execution of the test object was started</li>
 * <li>The execution result (@code OK} or {@code ERROR}</li>
 * <li>The name of the test object</li>
 * <li>The execution time in milliseconds</li>
 * <li>The error message in case of an error</li>
 * </ol>
 * </p>
 * <p>
 * The report file contains the collowing header line:<br />
 * {@code "timestamp";"result";"test object";"duration";"error message"}
 * </p>
 * 
 * @author rnaegele
 */
@ThreadSafe
public class SimpleReporter extends AbstractFileReporter {

	private static final char CSV_QUOTE = '"';
	private static final char DELIMITER = ';';

	// ISO8601 timestamp with milliseconds and timezone
	private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
	private static final FastDateFormat TIME_FORMAT = FastDateFormat.getInstance("HH:mm:ss.SSS");

	/**
	 * Creates a new instance with a default file name and the default character set.
	 */
	public SimpleReporter() {
		this(null, null);
	}

	/**
	 * Creates a new instance with a default file name.
	 * 
	 * @param charset
	 *            the character set for writing the report file
	 */
	public SimpleReporter(final Charset charset) {
		this(null, charset);
	}

	/**
	 * Creates a new instance with the default character set.
	 * 
	 * @param fileName
	 *            the name of the report file; is considered relative to the archive base directory
	 */
	public SimpleReporter(final String fileName) {
		super(fileName, null);
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param fileName
	 *            the name of the report file; is considered relative to the archive base directory
	 * @param charset
	 *            the character set for writing the report file
	 */
	public SimpleReporter(final String fileName, final Charset charset) {
		super(fileName, charset);
	}

	@Override
	public String getName() {
		return "SimpleReport";
	}

	@Override
	protected String getHeaderLine() {
		StringBuilder sb = new StringBuilder(255);
		appendEscapedAndQuoted(sb, "date");
		appendEscapedAndQuoted(sb, "start");
		appendEscapedAndQuoted(sb, "finish");
		appendEscapedAndQuoted(sb, "duration");
		appendEscapedAndQuoted(sb, "test object");
		appendEscapedAndQuoted(sb, "result");
		appendEscapedAndQuoted(sb, "error message");
		return sb.toString();
	}

	@Override
	protected String getFileExtension() {
		return "csv";
	}

	@Override
	public void addResult(final ReportContext reportContext) {
		log.debug("Adding result to reporter '{}'", getName());

		StringBuilder sb = new StringBuilder(255);

		appendEscapedAndQuoted(sb, DATE_FORMAT.format(reportContext.getStartMillis()));
		appendEscapedAndQuoted(sb, TIME_FORMAT.format(reportContext.getStartMillis()));
		appendEscapedAndQuoted(sb, TIME_FORMAT.format(reportContext.getStopMillis()));
		appendEscapedAndQuoted(sb,
				DurationFormatUtils.formatDurationHMS(reportContext.getStopMillis() - reportContext.getStartMillis()));
		appendEscapedAndQuoted(sb, reportContext.getTestObjectName());
		appendEscapedAndQuoted(sb, reportContext.isSuccess() ? JFunkConstants.OK : JFunkConstants.ERROR);

		Throwable th = reportContext.getThrowable();
		if (th != null) {
			String msg = th.getMessage();

			Throwable root = th;
			while (root.getCause() != null) {
				root = root.getCause();
			}

			String rootMsg = root.getMessage();
			if (rootMsg != null && !rootMsg.equals(msg)) {
				msg += " - Root Message: " + rootMsg;
			}

			if (isBlank(msg)) {
				msg = th.getClass().getName();
			}

			appendEscapedAndQuoted(sb, msg);
		} else {
			appendEscapedAndQuoted(sb, null);
		}

		synchronized (this) {
			reportLines.add(sb.toString());
		}
	}

	/**
	 * <p>
	 * Encloses the given value into double-quotes. Quote characters are escaped with an additional quote character. Line breaks
	 * are replaced with a space character. Multiple line breaks are collapsed to a single space.
	 * </p>
	 * <p>
	 * If the specified StringBuilder is non-empty, a semi-colon is appended first.
	 * </p>
	 * 
	 * @param sb
	 *            the string buffer the escaped and quoted result is appended to
	 * @param value
	 *            the input string to transform
	 */
	protected void appendEscapedAndQuoted(final StringBuilder sb, final String value) {
		boolean foundLineBreak = false;

		if (sb.length() > 0) {
			sb.append(DELIMITER);
		}

		sb.append(CSV_QUOTE);
		if (value != null) {
			for (int i = 0, len = value.length(); i < len; ++i) {
				char c = value.charAt(i);
				switch (c) {
					case CSV_QUOTE:
						if (foundLineBreak) {
							foundLineBreak = false;
							sb.append(' ');
						}
						sb.append(c); // escape double quote, i. e. add quote character again
						break;
					case '\r':
					case '\n':
						foundLineBreak = true;
						continue;
					default:
						if (foundLineBreak) {
							sb.append(' ');
							foundLineBreak = false;
						}
						break;
				}
				sb.append(c);
			}
		}
		sb.append(CSV_QUOTE);
	}
}
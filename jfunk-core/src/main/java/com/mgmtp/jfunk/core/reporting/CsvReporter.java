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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;

import com.google.common.collect.ImmutableList;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.data.DataSet;

/**
 * Reporter for writing test data and/or configuration properties to a CSV file. This reporter only
 * reports on module level. Steps are not reported, even if annotated with {@link Reported}. An
 * additional 'result' column is always written as the last column with values {@code OK} or
 * {@code ERROR}.
 * 
 * @author rnaegele
 */
@ThreadSafe
public class CsvReporter extends AbstractFileReporter {

	private static final String DEFAULT_DELIMITER = ";";
	private static final char NULL_CHAR = '\0';

	private final String delimiter;
	private final char quoteChar;
	private final boolean quoted;
	private final List<String> headers;

	private List<Column> columns;

	private final String dataSetKey;

	private String header;

	@Inject
	Provider<Map<String, DataSet>> currentDataSetsProvider;

	/**
	 * Creates a new instance.
	 * 
	 * @param fileName
	 *            the name of the file to write the report to (relative to the archive base
	 *            directory); if {@code null} it is computed from the reporter's class name and the
	 *            reporter's key
	 * @param headers
	 *            the header for the CSV report; if this param's value is {@code null}, all entries
	 *            from the data set specified by parameter {@code dataSetKey} are written to the
	 *            report with the header being the data set key plus the entry key seperated by a
	 *            space character
	 * @param delimiter
	 *            the column delimiter
	 * @param quoteChar
	 *            the quote character, if '\0', no quoting happens
	 * @param dataSetKey
	 *            the data set key; must be specified if and only if {@code headers} is {@code null}
	 * @param charset
	 *            the charset for the report
	 */
	CsvReporter(final String fileName, final List<String> headers, final String delimiter, final char quoteChar,
			final String dataSetKey, final Charset charset) {
		super(fileName, charset);

		if (headers == null) {
			checkArgument(dataSetKey != null, "If 'headers' is set to null, 'dataSetKey' must be set");
		} else {
			checkArgument(dataSetKey == null, "If 'headers' is not set to null, 'dataSetKey' must not be set");
		}

		this.delimiter = delimiter == null ? DEFAULT_DELIMITER : delimiter;
		this.quoteChar = quoteChar;
		this.quoted = quoteChar != NULL_CHAR;
		this.headers = headers != null ? ImmutableList.copyOf(headers) : null;
		this.dataSetKey = dataSetKey;
	}

	@Override
	public String getName() {
		return "CSV-Report";
	}

	@Override
	protected String getFileExtension() {
		return "csv";
	}

	private void initColumns() {
		StrBuilder sb = new StrBuilder(256);
		int i = 0;

		if (headers == null) {

			// Create a column for every data set key
			DataSet dataSet = currentDataSetsProvider.get().get(dataSetKey);
			checkNotNull(dataSet, "No data set available for key: " + dataSetKey);

			Set<String> keysSet = dataSet.getDataView().keySet();
			columns = newArrayListWithCapacity(keysSet.size());
			for (String key : keysSet) {
				columns.add(new Column(key, dataSetKey));
				appendEscapedAndQuoted(sb, i, dataSetKey + ' ' + key);
				i++;
			}

		} else {

			columns = newArrayListWithCapacity(headers.size());
			for (String h : headers) {
				columns.add(new Column(h));
				appendEscapedAndQuoted(sb, i, h);
				i++;
			}
		}

		// additional default columns
		appendEscapedAndQuoted(sb, i++, JFunkConstants.CURRENT_MODULE_NAME);
		appendEscapedAndQuoted(sb, i++, JFunkConstants.CURRENT_MODULE_RESULT);
		appendEscapedAndQuoted(sb, i++, JFunkConstants.CURRENT_MODULE_ERROR);

		header = sb.toString();
	}

	@Override
	protected synchronized String getHeaderLine() {
		if (header == null) {
			initColumns();
		}
		return header;
	}

	@Override
	public void addResult(final ReportData data) {
		if (!(data.getTestObject() instanceof TestModule)) {
			// we are only interested in the data for each module
			return;
		}

		log.debug("Adding result to reporter '{}'", getName());

		synchronized (this) {
			if (columns == null) {
				initColumns();
			}

			Map<String, DataSet> dataSets = currentDataSetsProvider.get();
			StrBuilder sb = new StrBuilder(256);
			int i = 0;
			for (Column column : columns) {
				String dsKey = column.dataSetKey;
				String value;

				if (StringUtils.isNotBlank(dsKey)) {
					// get value from data set
					DataSet ds = dataSets.get(dsKey);
					checkNotNull(ds, "No data set available for key: " + dsKey);
					value = ds.getValue(column.key);
				} else {
					// get property
					value = configProvider.get().get(column.key);
				}

				appendEscapedAndQuoted(sb, i, value);
				i++;
			}

			// additional result column
			appendEscapedAndQuoted(sb, i++, data.getTestObject().getName());
			appendEscapedAndQuoted(sb, i++, data.isSuccess() ? JFunkConstants.OK : JFunkConstants.ERROR);

			if (data.isSuccess()) {
				appendEscapedAndQuoted(sb, i++, "");
			} else {
				Throwable th = data.getThrowable();
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
				appendEscapedAndQuoted(sb, i++, msg);
			}

			if (sb.isEmpty()) {
				log.info("Ignoring empty row in report");
			} else {
				String line = sb.toString();
				reportLines.add(line);
			}
		}
	}

	private void appendEscapedAndQuoted(final StrBuilder sb, final int colIndex, final String value) {
		boolean foundLineBreak = false;

		sb.appendSeparator(delimiter, colIndex);

		if (quoted) {
			sb.append(quoteChar);
		}

		if (value != null) {
			for (int i = 0, len = value.length(); i < len; ++i) {
				char c = value.charAt(i);
				switch (c) {
					case '\r':
					case '\n':
						foundLineBreak = true;
						continue;
					default:
						if (foundLineBreak) {
							sb.append(' ');
							foundLineBreak = false;
						}
						if (quoted) { // can't have this as case because it is no constant expression
							sb.append(c); // escape double quote, i. e. add quote character again
						}
						break;
				}
				sb.append(c);
			}
		}

		if (quoted) {
			sb.append(quoteChar);
		}
	}

	static class Column {
		// Contains the name of the data set or {@code null} if {@link #key} represents a property.
		String dataSetKey;

		// Contains either a property or a data set entry key
		String key;

		Column(final String header) {
			String[] a = header.trim().split(" ");
			if (a.length == 2) {
				dataSetKey = a[0];
				this.key = a[1];
			} else if (a.length == 1) {
				this.key = header;
			} else {
				throw new IllegalArgumentException("Only one space is allowed as a separator: " + key
						+ " contains more than one space");
			}
		}

		Column(final String key, final String dataSetKey) {
			this.key = key;
			this.dataSetKey = dataSetKey;
		}
	}

	/**
	 * Creates a {@link CsvReporterBuilder} for a CSV report based on the {@link DataSet} with the
	 * specified key.
	 * 
	 * @param dataSetKey
	 *            the data set key
	 * @return the builder
	 */
	public static CsvReporterBuilder forDataSet(final String dataSetKey) {
		return new CsvReporterBuilder(dataSetKey);
	}

	/**
	 * Creates a {@link CsvReporterBuilder} for a CSV report based on the specified list of headers.
	 * 
	 * @param headers
	 *            the list of headers
	 * @return the builder
	 */
	public static CsvReporterBuilder withHeaders(final List<String> headers) {
		return new CsvReporterBuilder(headers);
	}

	/**
	 * A builder for creating {@link CsvReporter} instances.
	 * 
	 * @author rnaegele
	 */
	public static class CsvReporterBuilder {

		private String dataSetKey;
		private List<String> headers;
		private String delimiter;
		private char quoteChar;
		private String fileName;
		private Charset charset;

		CsvReporterBuilder(final String dataSetKey) {
			this.dataSetKey = dataSetKey;
		}

		CsvReporterBuilder(final List<String> headers) {
			this.headers = headers;
		}

		/**
		 * Sets the delimiter and returns the underlining builder instance.
		 * 
		 * @param aDelimiter
		 *            the column delimiter
		 * @return the builder
		 */
		public CsvReporterBuilder delimitedBy(final String aDelimiter) {
			delimiter = aDelimiter;
			return this;
		}

		/**
		 * Sets the quote character and returns the underlining builder instance.
		 * 
		 * @param aQuoteChar
		 *            the quote character, if '\0', no quoting happens
		 * @return the builder
		 */
		public CsvReporterBuilder quotedWith(final char aQuoteChar) {
			quoteChar = aQuoteChar;
			return this;
		}

		/**
		 * Sets the character set and returns the underlining builder instance.
		 * 
		 * @param aCharset
		 *            the charset for the report
		 * @return the builder
		 */
		public CsvReporterBuilder withCharset(final Charset aCharset) {
			charset = aCharset;
			return this;
		}

		/**
		 * Sets the file name and returns the underlining builder instance.
		 * 
		 * @param aFileName
		 *            the name of the file to write the report to (relative to the archive base
		 *            directory); if {@code null} it is computed from the reporter's class name and
		 *            the reporter's key
		 * @return the builder
		 */
		public CsvReporterBuilder writtenTo(final String aFileName) {
			fileName = aFileName;
			return this;
		}

		/**
		 * Creates a new {@link CsvReporter} instance from the underlying builder.
		 * 
		 * @return the reporter instance
		 */
		public CsvReporter create() {
			return new CsvReporter(fileName, headers, delimiter, quoteChar, dataSetKey, charset);
		}
	}
}
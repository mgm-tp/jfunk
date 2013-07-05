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
package com.mgmtp.jfunk.core.util;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayListWithCapacity;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrMatcher;
import org.apache.commons.lang3.text.StrTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharStreams;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.data.source.DataSource;

/**
 * <p>
 * Processes a CSV file. The class is used by the script command {@code processCsvFile}. The file
 * must have a header line. Each column header must either be the name of a {@link Configuration}
 * property or a {@link DataSet} key and {@link DataSet} entry key separated by a single space. The
 * file is iterated over line by line. The following happens on each iteration:
 * </p>
 * <p>
 * All columns are processed as follows:
 * <ul>
 * <li>If the column header is the name of a {@link Configuration} property, the column value is
 * stored in the {@link Configuration} under that name.</li>
 * <li>If the column header is a space-separated pair of {@link DataSet} key and {@link DataSet}
 * entry key, the column value is stored as fixed value in the respective {@link DataSet} with the
 * respective entry key. If the column value is {@code <auto>}, a potential fixed value is reset.</li>
 * </ul>
 * </p>
 * <p>
 * Sample CSV file:
 * 
 * <pre>
 * myProperty;login username;login password;checkout productId
 * foo;someUsername;somePassword;42
 * bar;someOtherUsername;someOtherPassword;{@literal <auto>}
 * </pre>
 * 
 * </p>
 * 
 * @author rnaegele
 */
@Singleton
@ThreadSafe
public class CsvDataProcessor {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Provider<Configuration> configProvider;
	private final Provider<DataSource> dataSourceProvider;

	@Inject
	CsvDataProcessor(final Provider<Configuration> configProvider, final Provider<DataSource> dataSourceProvider) {
		this.configProvider = configProvider;
		this.dataSourceProvider = dataSourceProvider;
	}

	/**
	 * Processes the specified CSV file. For every line but the header line (which is required), the
	 * specified command is executed.
	 * 
	 * @param reader
	 *            the reader for loading the CSV data
	 * @param delimiter
	 *            the column separator
	 * @param quoteChar
	 *            the quote character ('\0' for no quoting)
	 * @param command
	 *            the command (i. e. a Groovy closure if used in a Groovy script) to be executed for
	 *            every processed line
	 */
	public void processFile(final Reader reader, final String delimiter, final char quoteChar, final Runnable command) {
		try {
			List<String> inputLines = CharStreams.readLines(reader);

			StrTokenizer st = StrTokenizer.getCSVInstance();
			st.setDelimiterString(delimiter);
			if (quoteChar != '\0') {
				st.setQuoteChar(quoteChar);
			} else {
				st.setQuoteMatcher(StrMatcher.noneMatcher());
			}

			// extract header
			String headerLine = inputLines.remove(0);
			List<Column> columns = initColumns(st, headerLine);
			for (String line : inputLines) {
				st.reset(line);
				String[] colArray = st.getTokenArray();
				int len = colArray.length;
				checkState(len == columns.size(), "Mismatch between number of header columns and number of line columns.");

				DataSource dataSource = dataSourceProvider.get();
				Configuration config = configProvider.get();
				for (int i = 0; i < len; ++i) {
					String value = StringUtils.trimToEmpty(colArray[i]);

					String dataSetKey = columns.get(i).dataSetKey;
					String key = columns.get(i).key;
					if (dataSetKey != null) {
						if ("<auto>".equals(value)) {
							dataSource.resetFixedValue(dataSetKey, key);
						} else {
							log.debug("Setting data set entry for " + this + " to value=" + value);
							dataSource.setFixedValue(dataSetKey, key, value);
						}
					} else {
						log.debug("Setting property for " + this + " to value=" + value);
						config.put(key, value);
					}
				}

				command.run();
			}
		} catch (IOException ex) {
			throw new JFunkException("Error processing CSV data", ex);
		}
	}

	private List<Column> initColumns(final StrTokenizer st, final String headerLine) {
		st.reset(headerLine);

		String[] headers = st.getTokenArray();
		List<Column> columns = newArrayListWithCapacity(headers.length);
		for (String header : headers) {
			columns.add(new Column(header));
		}
		return columns;
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
				throw new IllegalArgumentException("Only one space is allowed as a separator: " + key + " contains more than one space");
			}
		}
	}
}
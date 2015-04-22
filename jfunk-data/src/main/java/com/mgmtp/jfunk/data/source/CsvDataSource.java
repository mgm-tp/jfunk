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
package com.mgmtp.jfunk.data.source;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrTokenizer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.mgmtp.jfunk.common.config.ScriptScoped;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.data.DefaultDataSet;

/**
 * {@link DataSource} implementation for CSV files. CSV files must be valid and especially comply
 * with the following rules:
 * <ul>
 * <li>The separator character is a semicolon.</li>
 * <li>Values may be quoted with double quotes. Quotes must be escaped with the quote symbol itself.
 * </li>
 * <li>Empty lines, lines containing whitespace only, and lines starting with a "#" symbol are
 * ignored.</li>
 * </ul>
 * 
 */
@ScriptScoped
public class CsvDataSource extends BaseDataSource {

	private Map<String, CsvFile> csvFiles;

	@Inject
	public CsvDataSource(final Configuration configuration) {
		super(configuration);
	}

	/**
	 * CSV files are not loaded here, this is done on-access (lazy loading).
	 */
	protected Map<String, CsvFile> getCsvFiles() {
		if (csvFiles == null) {
			String fileName = null;
			csvFiles = Maps.newHashMap();

			Set<String> keys = configuration.keySet();
			String prefix = "dataSource." + getName() + ".";
			for (String key : keys) {
				if (key.startsWith(prefix)) {
					String dataKey = key.substring(key.indexOf(prefix) + prefix.length());
					fileName = configuration.get(key);
					if (StringUtils.isEmpty(fileName)) {
						// no CSV file configured
						continue;
					}
					log.debug("Loading file " + fileName + " for dataKey=" + dataKey);
					CsvFile csvFile = new CsvFile(fileName);
					csvFiles.put(dataKey, csvFile);
				}
			}
			if (csvFiles.isEmpty()) {
				log.warn("CsvDataSource can only be used when CSV files are correctly configured");
			}
		}
		return csvFiles;
	}

	@Override
	protected DataSet getNextDataSetImpl(final String key) {
		CsvFile csvFile = getCsvFiles().get(key);
		if (csvFile != null) {
			if (csvFile.lines == null) {
				try {
					csvFile.load();
				} catch (IOException e) {
					throw new IllegalArgumentException("Could not load CSV file " + csvFile.fileName, e);
				}
			}
			Map<String, String> data = csvFile.getNextLineMap();
			return new DefaultDataSet(data);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return {@code false}, as soon as the specified CSV file has no more lines available.
	 */
	@Override
	public boolean hasMoreData(final String dataSetKey) {
		if (getCsvFiles().containsKey(dataSetKey)) {
			CsvFile csvFile = getCsvFiles().get(dataSetKey);
			if (csvFile.lines == null) {
				try {
					csvFile.load();
				} catch (IOException e) {
					throw new IllegalArgumentException("Could not load CSV file " + csvFile.fileName, e);
				}
			}
			return csvFile.hasNextLine();
		}
		return false;
	}

	@Override
	public void doReset() {
		csvFiles = null;
	}

	/**
	 * Represents a CSV file. The file may only be processed once after calling the {@link #load()}
	 * method.
	 */
	static class CsvFile {
		private List<String> headers;
		protected final String fileName;
		protected Queue<List<String>> lines;

		/**
		 * Creates a new CsvFile instance.
		 * 
		 * @param fileName
		 *            The file name.
		 */
		public CsvFile(final String fileName) {
			this.fileName = fileName;
		}

		/**
		 * Loads the CSV file from the file system.
		 */
		public void load() throws IOException {
			lines = Lists.newLinkedList();
			headers = null;

			StrTokenizer st = StrTokenizer.getCSVInstance();
			st.setDelimiterChar(';');

			// Default encoding is used (--> UTF-8).
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(fileName));
				for (String line = null; (line = br.readLine()) != null;) {
					String trimmedLine = StringUtils.trimToNull(line);
					if (trimmedLine == null || trimmedLine.startsWith("#")) {
						continue;
					}
					st.reset(line);
					ArrayList<String> tokens = Lists.newArrayList(st.getTokenArray());
					if (headers == null) {
						headers = tokens;
					} else {
						lines.add(tokens);
					}
				}
			} finally {
				IOUtils.closeQuietly(br);
			}
		}

		/**
		 * Polls for the next line in the CSV file.
		 * 
		 * @return The next line as map of header/value pairs, or null, if no further line is
		 *         available.
		 */
		public Map<String, String> getNextLineMap() {
			List<String> line = lines.poll();
			if (line == null) {
				return null;
			}

			Map<String, String> result = Maps.newHashMapWithExpectedSize(headers.size());
			for (ListIterator<String> li = line.listIterator(); li.hasNext();) {
				int index = li.nextIndex();
				String header = headers.get(index);
				String value = li.next();
				if (StringUtils.isBlank(header)) {
					continue;
				}
				result.put(header, value);
			}
			return result;
		}

		/**
		 * Checks whether a next line is available.
		 * 
		 * @return {@code true}, if a next line is available.
		 */
		public boolean hasNextLine() {
			return !lines.isEmpty();
		}
	}
}

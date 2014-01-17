/*
 * Copyright (c) 2014 mgm technology partners GmbH
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

import static com.google.common.collect.Maps.newHashMap;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.inject.Inject;
import com.mgmtp.jfunk.common.config.ScriptScoped;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.common.util.ExtendedProperties;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.data.DefaultDataSet;

/**
 * {@link DataSource} implementation which takes properties from an existing test archive.
 * 
 */
@ScriptScoped
public class ArchiveDataSource extends BaseDataSource {

	private static final Pattern KEY_PATTERN = Pattern.compile("formdata/(.*)\\.form\\.properties");

	private Map<String, DataSet> dataSets;

	@Inject
	public ArchiveDataSource(final Configuration configuration) {
		super(configuration);
	}

	private Map<String, DataSet> getDataSets() {
		if (dataSets == null) {
			String archiveFileName = configuration.get("dataSource." + getName() + ".archiveFileName");
			if (archiveFileName == null) {
				throw new IllegalStateException("No archive files configured.");
			}
			log.info("Using " + archiveFileName);

			dataSets = newHashMap();
			try {
				ZipFile zip = new ZipFile(archiveFileName);
				for (Enumeration<? extends ZipEntry> zipEntryEnum = zip.entries(); zipEntryEnum.hasMoreElements();) {
					ZipEntry zipEntry = zipEntryEnum.nextElement();
					String name = zipEntry.getName();
					Matcher matcher = KEY_PATTERN.matcher(name);
					if (matcher.find()) {
						InputStream is = null;
						try {
							is = zip.getInputStream(zipEntry);
							String key = matcher.group(1);
							ExtendedProperties props = new ExtendedProperties();
							props.load(is);
							log.debug("Adding data for key=" + key);
							dataSets.put(key, new DefaultDataSet(props));
						} finally {
							closeQuietly(is);
						}
					}
				}
			} catch (IOException ex) {
				throw new JFunkException("Error getting data sets.", ex);
			}
		}
		return dataSets;
	}

	@Override
	protected DataSet getNextDataSetImpl(final String key) {
		Map<String, DataSet> ds = getDataSets();
		if (ds.containsKey(key)) {
			return ds.get(key).copy();
		}
		return null;
	}

	/**
	 * Always returns {@code true}, which means that this {@link DataSource} implementation would
	 * always return the same data.
	 * 
	 * @return {@code true}
	 */
	@Override
	public boolean hasMoreData(final String dataSetKey) {
		return true;
	}

	@Override
	public void doReset() {
		dataSets = null;
	}
}
/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.source;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.common.util.ExtendedProperties;
import com.mgmtp.jfunk.common.util.Predicates;
import com.mgmtp.jfunk.data.DataSet;

/**
 * Abstract {@link DataSource} implementation with functionality for handling fixed values.
 * 
 * @version $Id$
 */
public abstract class BaseDataSource implements DataSource {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected final Map<String, Map<String, String>> fixedValues = Maps.newHashMap();
	protected final Configuration configuration;
	private Set<String> formDataKeys;
	private final String name;
	private final Map<String, DataSet> currentDataSets = Maps.newHashMap();

	/**
	 * Creates a new instance of the data source, reading fixed properties from the configuration.
	 * 
	 * @param configuration
	 *            Configuration for the data source.
	 */
	protected BaseDataSource(final Configuration configuration) {
		this.name = StringUtils.uncapitalize(StringUtils.substringBefore(getClass().getSimpleName(), "DataSource"));
		this.configuration = configuration;
	}

	protected Set<String> getFormDataKeys() {
		try {
			Map<String, String> view = Maps.filterKeys(configuration, Predicates.startsWith(JFunkConstants.FORM_DATA_PREFIX));
			formDataKeys = Sets.newHashSetWithExpectedSize(view.size());

			for (String key : view.keySet()) {
				String fixedPropsFilenameKey = key;
				int i = fixedPropsFilenameKey.lastIndexOf('.');
				String dataKey = fixedPropsFilenameKey.substring(9, i);
				formDataKeys.add(dataKey);

				// Load fixed properties
				String fixedPropsFilename = configuration.get(fixedPropsFilenameKey);
				if (StringUtils.isNotEmpty(fixedPropsFilename)) {
					InputStream is = null;
					try {
						ExtendedProperties fixedProps = new ExtendedProperties();
						is = configuration.openStream(fixedPropsFilename);
						fixedProps.load(is);

						for (Entry<String, String> entry : fixedProps.entrySet()) {
							setFixedValue(dataKey, entry.getKey(), entry.getValue());
						}
					} finally {
						IOUtils.closeQuietly(is);
					}
				}
			}
		} catch (IOException ex) {
			throw new JFunkException("Error loading form data keys.", ex);
		}
		return formDataKeys;
	}

	/**
	 * Returns the next {@link DataSet} for the specified key. Implementations must override this
	 * method.
	 * 
	 * @param key
	 *            The key.
	 * @return The {@link DataSet}, or null, if none is available.
	 */
	protected abstract DataSet getNextDataSetImpl(String key);

	/**
	 * Returns the next {@link DataSet} for the specified key. Fixed values are applied to the
	 * result.
	 * 
	 * @param key
	 *            The key.
	 * @return The {@link DataSet}, or null, if none is available.
	 */
	@Override
	public DataSet getNextDataSet(final String key) {
		DataSet data = getNextDataSetImpl(key);
		if (data != null) {
			copyFixedValues(key, data);
			currentDataSets.put(key, data);
		}
		return data;
	}

	@Override
	public DataSet getCurrentDataSet(final String key) {
		return currentDataSets.get(key);
	}

	@Override
	public Map<String, DataSet> getCurrentDataSets() {
		return currentDataSets;
	}

	private void copyFixedValues(final String key, final DataSet data) {
		Map<String, String> map = fixedValues.get(key);
		if (map != null) {
			for (Entry<String, String> entry : map.entrySet()) {
				data.setFixedValue(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Resets (= removes) a specific fixed value from the specified {@link DataSet}.
	 * 
	 * @param dataSetKey
	 *            The {@link DataSet} key.
	 * @param entryKey
	 *            The entry key.
	 */
	@Override
	public void resetFixedValue(final String dataSetKey, final String entryKey) {
		Map<String, String> map = fixedValues.get(dataSetKey);
		if (map != null) {
			if (map.remove(entryKey) == null) {
				log.warn("Entry " + dataSetKey + "." + entryKey + " could not be found in map of fixed values");
			}
			if (map.isEmpty()) {
				fixedValues.remove(dataSetKey);
			}
		}
		DataSet dataSet = getCurrentDataSets().get(dataSetKey);
		if (dataSet != null) {
			dataSet.resetFixedValue(entryKey);
		}
	}

	/**
	 * Resets (= removes) fixed values for the specified {@link DataSet} key.
	 * 
	 * @param dataSetKey
	 *            The {@link DataSet} key.
	 */
	@Override
	public void resetFixedValues(final String dataSetKey) {
		fixedValues.remove(dataSetKey);
		DataSet dataSet = getCurrentDataSets().get(dataSetKey);
		if (dataSet != null) {
			dataSet.resetFixedValues();
		}
	}

	/**
	 * Resets (= removes) all fixed values.
	 */
	@Override
	public void resetFixedValues() {
		fixedValues.clear();
		for (DataSet ds : getCurrentDataSets().values()) {
			ds.resetFixedValues();
		}
	}

	/**
	 * Sets a fixed value.
	 * 
	 * @param dataSetKey
	 *            The {@link DataSet} key.
	 * @param entryKey
	 *            The entry key.
	 * @param value
	 *            The fixed value.
	 */
	@Override
	public void setFixedValue(final String dataSetKey, final String entryKey, final String value) {
		Map<String, String> map = fixedValues.get(dataSetKey);
		if (map == null) {
			map = Maps.newHashMap();
			fixedValues.put(dataSetKey, map);
		}
		map.put(entryKey, value);

		DataSet dataSet = getCurrentDataSet(dataSetKey);
		if (dataSet != null) {
			dataSet.setFixedValue(entryKey, value);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void copyDataSetKey(final String key, final String newKey) {
		if (currentDataSets.containsKey(key)) {
			DataSet data = currentDataSets.get(key);
			currentDataSets.put(newKey, data);
		}
	}

	@Override
	public void removeDataSet(final String key) {
		if (currentDataSets.containsKey(key)) {
			currentDataSets.remove(key);
		}
	}

	/**
	 * Derived classes have to reset themselves.
	 */
	protected abstract void doReset();

	@Override
	public void reset() {
		formDataKeys = null;
		currentDataSets.clear();
		fixedValues.clear();
		// Derived classes have to reset themselves
		doReset();
		log.debug("Finished reset");
	}
}

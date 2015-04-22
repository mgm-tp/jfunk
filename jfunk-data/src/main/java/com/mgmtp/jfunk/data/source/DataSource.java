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

import java.util.Map;

import com.mgmtp.jfunk.data.DataSet;

/**
 * Interface for a data source. A DataSource provides test data for jFunk tests.
 * 
 */
public interface DataSource {

	/**
	 * Returns the next {@link DataSet} for the specified key.
	 * 
	 * @param key
	 *            The key.
	 * @return The {@link DataSet}, or null, if none is available.
	 */
	DataSet getNextDataSet(String key);

	/**
	 * Returns the current {@link DataSet} for the specified key. A current {@link DataSet} is only
	 * available if {@link #getNextDataSet(String)} with the same key has already been called
	 * before.
	 * 
	 * @param key
	 *            The key.
	 * @return The {@link DataSet}, or null, if none is available.
	 */
	DataSet getCurrentDataSet(String key);

	/**
	 * Returns a {@link Map} of the currently available {@link DataSet}s. Availability of current
	 * {@link DataSet}s depends on previous call to {@link #getNextDataSet(String)}.
	 * 
	 * @return The {@link Map} of {@link DataSet}s. May be empty, if no {@link DataSet}s are
	 *         available.
	 */
	Map<String, DataSet> getCurrentDataSets();

	/**
	 * Checks if more data is available for a specified key.
	 * 
	 * @return {@code true}, if more data is available.
	 */
	boolean hasMoreData(String dataSetKey);

	/**
	 * Sets a value that overrides the one from the {@link DataSource}.
	 * 
	 * @param dataSetKey
	 *            The key of the {@link DataSet}.
	 * @param entryKey
	 *            The key of the value.
	 * @param value
	 *            The fixed value.
	 */
	void setFixedValue(String dataSetKey, String entryKey, String value);

	/**
	 * Resets a specific fixed value in a {@link DataSet}.
	 * 
	 * @param dataSetKey
	 *            The {@link DataSet} key.
	 * @param entryKey
	 *            The entry key.
	 */
	void resetFixedValue(String dataSetKey, String entryKey);

	/**
	 * Resets all fixed values for the specified {@link DataSet}.
	 * 
	 * @param dataSetKey
	 *            The {@link DataSet} key.
	 */
	void resetFixedValues(String dataSetKey);

	/**
	 * Resets all fixed values.
	 */
	void resetFixedValues();

	/**
	 * Returns the name of the data source.
	 * 
	 * @return The name.
	 */
	String getName();

	/**
	 * Copies a data set such that it is available under a new name. This is no deep copy of the
	 * data set. It merely creates a new reference to the same object.
	 * 
	 * @param oldKey
	 *            the name of the data set to be copied
	 * @param newKey
	 *            the new name we want to access the data set by
	 */
	void copyDataSetKey(String oldKey, String newKey);

	/**
	 * Removes a data set from this data source
	 * 
	 * @param key
	 *            the name of the data set to be removed
	 */
	void removeDataSet(String key);

	/**
	 * Resets the data source so that e.g. calling {@code init} in a script freshly initialises the
	 * data source.
	 */
	void reset();
}
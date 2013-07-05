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
package com.mgmtp.jfunk.core.step.base;

import java.util.Map;

import javax.inject.Inject;

import com.mgmtp.jfunk.data.DataSet;

/**
 * Base class for steps that need {@link DataSet}s. Gets the current data sets injected.
 * 
 * @author rnaegele
 */
public abstract class DataSetsStep extends BaseStep {

	private Map<String, DataSet> dataSets;

	/**
	 * the data set key, which used by method {@link #getDataSet()}
	 */
	private final String dataSetKey;

	/**
	 * @param dataSetKey
	 *            the key used by the method {@link #getDataSet()} for retrieving the data set from
	 *            the map of all available data sets
	 */
	public DataSetsStep(final String dataSetKey) {
		this(null, dataSetKey);
	}

	/**
	 * @param name
	 *            the step's name
	 * @param dataSetKey
	 *            the key used by the method {@link #getDataSet()} for retrieving the data set from
	 *            the map of all available data sets
	 */
	public DataSetsStep(final String name, final String dataSetKey) {
		super(name);
		this.dataSetKey = dataSetKey;
	}

	/**
	 * @return the dataSets
	 */
	public Map<String, DataSet> getDataSets() {
		return dataSets;
	}

	/**
	 * @param dataSets
	 *            the dataSets to set
	 */
	@Inject
	public void setDataSets(final Map<String, DataSet> dataSets) {
		this.dataSets = dataSets;
	}

	/**
	 * @return the dataSets
	 */
	public DataSet getDataSet(final String dsKey) {
		return dataSets.get(dsKey);
	}

	/**
	 * Returns the step's default {@link DataSet}.
	 * 
	 * @return the step's default {@link DataSet}
	 */
	public DataSet getDataSet() {
		return dataSetKey != null ? dataSets.get(dataSetKey) : null;
	}

	/**
	 * @return the dataSetKey
	 */
	public String getDataSetKey() {
		return dataSetKey;
	}
}

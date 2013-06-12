/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
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

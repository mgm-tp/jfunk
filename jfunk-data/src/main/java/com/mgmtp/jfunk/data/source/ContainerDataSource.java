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
package com.mgmtp.jfunk.data.source;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mgmtp.jfunk.common.config.ScriptScoped;
import com.mgmtp.jfunk.data.DataSet;

/**
 * DataSource implementation that includes several data sources for {@link DataSet} lookup. If one
 * DataSource cannot deliver a {@link DataSet}, the next one is queried and so on.
 * 
 */
@ScriptScoped
public class ContainerDataSource implements DataSource {

	private final Provider<List<? extends DataSource>> dataSourcesProvider;
	private final String name;

	@Inject
	public ContainerDataSource(final Provider<List<? extends DataSource>> dataSourcesProvider) {
		this.dataSourcesProvider = dataSourcesProvider;
		this.name = StringUtils.uncapitalize(StringUtils.substringBefore(getClass().getSimpleName(), "DataSource"));
	}

	@Override
	public DataSet getNextDataSet(final String key) {
		List<? extends DataSource> dataSources = dataSourcesProvider.get();
		for (DataSource ds : dataSources) {
			DataSet data = ds.getNextDataSet(key);
			if (data != null) {
				return data;
			}
		}
		return null;
	}

	@Override
	public DataSet getCurrentDataSet(final String key) {
		List<? extends DataSource> dataSources = dataSourcesProvider.get();
		for (DataSource ds : dataSources) {
			DataSet data = ds.getCurrentDataSet(key);
			if (data != null) {
				return data;
			}
		}
		return null;
	}

	@Override
	public Map<String, DataSet> getCurrentDataSets() {
		Map<String, DataSet> result = Maps.newHashMap();
		List<? extends DataSource> dataSources = dataSourcesProvider.get();
		for (DataSource ds : dataSources) {
			if (result == null) {
				result = Maps.newHashMap(ds.getCurrentDataSets());
			} else {
				collectDataSets(result, ds.getCurrentDataSets());
			}
		}
		return result;
	}

	private void collectDataSets(final Map<String, DataSet> result, final Map<String, DataSet> additionalDataSets) {
		for (Entry<String, DataSet> entry : additionalDataSets.entrySet()) {
			String key = entry.getKey();
			if (!result.containsKey(key)) {
				result.put(key, entry.getValue());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return {@code true}, as long as any of the contained data sources has more data available
	 *         for the specified key
	 */
	@Override
	public boolean hasMoreData(final String dataSetKey) {
		List<? extends DataSource> dataSources = dataSourcesProvider.get();
		for (DataSource ds : dataSources) {
			if (ds.hasMoreData(dataSetKey)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void resetFixedValue(final String dataSetKey, final String entryKey) {
		List<? extends DataSource> dataSources = dataSourcesProvider.get();
		for (DataSource ds : dataSources) {
			ds.resetFixedValue(dataSetKey, entryKey);
		}
	}

	@Override
	public void resetFixedValues(final String dataSetKey) {
		List<? extends DataSource> dataSources = dataSourcesProvider.get();
		for (DataSource ds : dataSources) {
			ds.resetFixedValues(dataSetKey);
		}
	}

	@Override
	public void resetFixedValues() {
		List<? extends DataSource> dataSources = dataSourcesProvider.get();
		for (DataSource ds : dataSources) {
			ds.resetFixedValues();
		}
	}

	@Override
	public void setFixedValue(final String dataSetKey, final String entryKey, final String value) {
		List<? extends DataSource> dataSources = dataSourcesProvider.get();
		for (DataSource ds : dataSources) {
			ds.setFixedValue(dataSetKey, entryKey, value);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void copyDataSetKey(final String key, final String newKey) {
		List<? extends DataSource> dataSources = dataSourcesProvider.get();
		for (DataSource ds : dataSources) {
			ds.copyDataSetKey(key, newKey);
		}
	}

	@Override
	public void removeDataSet(final String key) {
		List<? extends DataSource> dataSources = dataSourcesProvider.get();
		for (DataSource ds : dataSources) {
			ds.removeDataSet(key);
		}
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		tsb.append("name", name);
		tsb.append("dataSources", dataSourcesProvider);
		return tsb.toString();
	}

	@Override
	public void reset() {
		List<? extends DataSource> dataSources = dataSourcesProvider.get();
		for (DataSource ds : dataSources) {
			ds.reset();
		}
	}
}

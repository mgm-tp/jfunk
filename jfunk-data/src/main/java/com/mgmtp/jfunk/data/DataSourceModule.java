package com.mgmtp.jfunk.data;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

import java.util.List;
import java.util.Map;

import javax.inject.Provider;

import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.config.ScriptScoped;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.source.ArchiveDataSource;
import com.mgmtp.jfunk.data.source.ContainerDataSource;
import com.mgmtp.jfunk.data.source.CsvDataSource;
import com.mgmtp.jfunk.data.source.DataSource;

/**
 * Guice module for the standard data sources.
 * 
 * @author rnaegele
 * @version $Id$
 */
public class DataSourceModule extends BaseDataSourceModule {

	@Override
	protected void doConfigure() {
		bindDataSource("csv").to(CsvDataSource.class);
		bindDataSource("container").to(ContainerDataSource.class);
		bindDataSource("archive").to(ArchiveDataSource.class);
	}

	@Provides
	@ScriptScoped
	DataSource provideDataSource(final Provider<Map<String, DataSource>> dataSourceProvider, final Configuration configuration) {
		String dsName = configuration.get(JFunkConstants.DATA_SOURCE_NAME, false);
		return dataSourceProvider.get().get(dsName);
	}

	@Provides
	Map<String, DataSet> provideCurrentDataSets(final Provider<DataSource> dataSourceProvider) {
		return dataSourceProvider.get().getCurrentDataSets();
	}

	/**
	 * Reads child datasources for the ContainerDataSource from the configuration.
	 */
	@Provides
	List<? extends DataSource> provideContainerChildDataSources(final Configuration configuration, final Map<String, DataSource> availableDataSources) {

		List<DataSource> dataSources = newArrayListWithExpectedSize(3);
		String key = "dataSource.container.dsref";

		for (int i = 1;; ++i) {
			String dsKey = key + i;
			String dsName = configuration.get(dsKey);
			if (dsName == null) {
				break;
			}
			DataSource ds = availableDataSources.get(dsName);
			if (ds == null) {
				throw new ProvisionException("DataSource with name '" + dsName + "' not available. Check your configuration.");
			}
			dataSources.add(ds);
		}

		return dataSources;
	}

}

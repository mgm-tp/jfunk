package com.mgmtp.jfunk.core.data;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.log4j.Logger;

import com.google.common.collect.ForwardingMap;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.data.source.DataSource;

/**
 * Adapter class so {@link DataSet DataSets} may be accessed via {@link Configuration}.
 * 
 * @version $Id$
 */
public class DataSetAdapter extends ForwardingMap<String, String> {

	private final Logger log = Logger.getLogger(getClass());
	private final Provider<DataSource> dataSourceProvider;

	@Inject
	public DataSetAdapter(final Provider<DataSource> dataSourceProvider) {
		this.dataSourceProvider = dataSourceProvider;
	}

	@Override
	protected Map<String, String> delegate() {
		return Collections.emptyMap();
	}

	@Override
	public String get(final Object key) {
		String[] pair = getKeysPair(key);
		if (pair.length == 1) {
			return null;
		}
		String dsKey = pair[0];
		DataSet currentDataSet = dataSourceProvider.get().getCurrentDataSet(dsKey);
		if (currentDataSet == null) {
			log.warn("No data set available: '" + dsKey + "'. Forgot to call 'generate?'");
			return null;
		}
		String value = pair[1];
		String[] split = value.split(JFunkConstants.INDEXED_KEY_SEPARATOR);
		return split.length > 1 ? currentDataSet.getValue(split[0], Integer.parseInt(split[1])) : currentDataSet.getValue(split[0]);
	}

	private String[] getKeysPair(final Object key) {
		return ((String) key).split("\\s+");
	}
}
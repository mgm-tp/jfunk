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
package com.mgmtp.jfunk.data;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mgmtp.jfunk.common.JFunkConstants;

/**
 * Default {@link DataSet} implementation including functionality for storing fixed values.
 * Null-values are trimmed to the empty {@link String}.
 *
 */
public class DefaultDataSet implements DataSet {

	protected Map<String, String> data;
	protected final Map<String, String> fixedValues = Maps.newHashMap();
	public static final Logger LOG = Logger.getLogger(DefaultDataSet.class);

	public DefaultDataSet() {
		Map<String, String> map = Maps.newHashMap();
		init(map);
	}

	public DefaultDataSet(final Map<String, String> data) {
		Map<String, String> map = Maps.newHashMap(data);
		init(map);
	}

	private void init(final Map<String, String> map) {
		data = new ForwardingMap<String, String>() {
			@Override
			protected Map<String, String> delegate() {
				return map;
			}

			@Override
			public String get(final Object key) {
				String ov = fixedValues.get(key);
				return ov == null ? delegate().get(key) : ov;
			}

			@Override
			public boolean containsKey(final Object key) {
				return fixedValues.containsKey(key) || delegate().containsKey(key);
			}

			@Override
			public Set<Entry<String, String>> entrySet() {
				Set<String> fixedValueKeys = fixedValues.keySet();
				Set<Entry<String, String>> result = Sets.newHashSet(fixedValues.entrySet());
				for (Entry<String, String> entry : delegate().entrySet()) {
					if (!fixedValueKeys.contains(entry.getKey())) {
						result.add(entry);
					}
				}
				return result;
			}

			@Override
			public Set<String> keySet() {
				Set<String> keys = Sets.newHashSet(fixedValues.keySet());
				keys.addAll(delegate().keySet());
				return keys;
			}
		};
	}

	@Override
	public boolean hasValue(final String key) {
		return data.containsKey(key);
	}

	@Override
	public boolean hasValue(final String key, final int index) {
		return data.containsKey(key + JFunkConstants.INDEXED_KEY_SEPARATOR + index);
	}

	@Override
	public String getValue(final String key) {
		return StringUtils.defaultString(data.get(key), "");
	}

	@Override
	public String getValue(final String key, final int index) {
		return getValue(key + JFunkConstants.INDEXED_KEY_SEPARATOR + index);
	}

	@Override
	public void setValue(final String key, final String value) {
		String result = data.put(key, value);
		if (result != null) {
			LOG.debug("Previously assigned value=" + result + " for key=" + key + " is now replaced by value=" + value);
		}
		String fixedVal = fixedValues.get(key);
		if (fixedVal != null) {
			LOG.warn("A fixed value=" + fixedVal + " already exists for key=" + key + " so value=" + value
					+ " set right now might be ignored when calling getValue");
		}
	}

	@Override
	public void setValue(final String key, final int index, final String value) {
		setValue(key + JFunkConstants.INDEXED_KEY_SEPARATOR + index, value);
	}

	@Override
	public boolean getValueAsBoolean(final String key) {
		String value = getValue(key);
		return Boolean.parseBoolean(value);
	}

	@Override
	public boolean getValueAsBoolean(final String key, final int index) {
		String value = getValue(key, index);
		return Boolean.parseBoolean(value);
	}

	@Override
	public Integer getValueAsInteger(final String key) {
		String value = getValue(key);
		return StringUtils.isEmpty(value) ? null : Integer.valueOf(value);
	}

	@Override
	public Integer getValueAsInteger(final String key, final int index) {
		String value = getValue(key, index);
		return StringUtils.isEmpty(value) ? null : Integer.valueOf(value);
	}

	@Override
	public Double getValueAsDouble(final String key) {
		String value = getValue(key);
		return StringUtils.isEmpty(value) ? null : Double.valueOf(value);
	}

	@Override
	public Double getValueAsDouble(final String key, final int index) {
		String value = getValue(key, index);
		return StringUtils.isEmpty(value) ? null : Double.valueOf(value.replace(',', '.'));
	}

	@Override
	public Map<String, String> getDataView() {
		return Collections.unmodifiableMap(data);
	}

	@Override
	public void setFixedValue(final String key, final String value) {
		fixedValues.put(key, value);
	}

	@Override
	public String removeValue(final String key) {
		return data.remove(key);
	}

	@Override
	public String removeValue(final String key, final int index) {
		return data.remove(key + JFunkConstants.INDEXED_KEY_SEPARATOR + index);
	}

	@Override
	public void resetFixedValue(final String key) {
		fixedValues.remove(key);
	}

	@Override
	public void resetFixedValues() {
		fixedValues.clear();
	}

	@Override
	public DataSet copy() {
		DefaultDataSet copy = new DefaultDataSet(data);
		for (Entry<String, String> entry : fixedValues.entrySet()) {
			copy.setFixedValue(entry.getKey(), entry.getValue());
		}
		return copy;
	}

	@Override
	public String toString() {
		return data.toString();
	}

	@Override
	public boolean containsKey(final String key) {
		return data.containsKey(key) || fixedValues.containsKey(key);
	}

	@Override
	public boolean containsKey(final String key, final int index) {
		return data.containsKey(key + JFunkConstants.INDEXED_KEY_SEPARATOR + index)
				|| fixedValues.containsKey(key + JFunkConstants.INDEXED_KEY_SEPARATOR + index);
	}
}
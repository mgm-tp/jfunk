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
package com.mgmtp.jfunk.data;

import java.util.Collections;
import java.util.Map;

/**
 * {@link DataSet} implementation which is empty and immutable.
 * 
 */
public final class EmptyDataSet implements DataSet {

	public static final EmptyDataSet INSTANCE = new EmptyDataSet();

	private EmptyDataSet() {
		// nothing to do
	}

	@Override
	public DataSet copy() {
		return this;
	}

	@Override
	public Map<String, String> getDataView() {
		return Collections.emptyMap();
	}

	@Override
	public String getValue(final String key) {
		return "";
	}

	@Override
	public String getValue(final String key, final int index) {
		return getValue(key);
	}

	@Override
	public boolean getValueAsBoolean(final String key) {
		return false;
	}

	@Override
	public boolean getValueAsBoolean(final String key, final int index) {
		return false;
	}

	@Override
	public Double getValueAsDouble(final String key) {
		return null;
	}

	@Override
	public Double getValueAsDouble(final String key, final int index) {
		return null;
	}

	@Override
	public Integer getValueAsInteger(final String key) {
		return null;
	}

	@Override
	public Integer getValueAsInteger(final String key, final int index) {
		return null;
	}

	@Override
	public boolean hasValue(final String key) {
		return false;
	}

	@Override
	public boolean hasValue(final String key, final int index) {
		return false;
	}

	@Override
	public String removeValue(final String key) {
		return null;
	}

	@Override
	public String removeValue(final String key, final int index) {
		return null;
	}

	@Override
	public void resetFixedValue(final String key) {
		// no-op
	}

	@Override
	public void resetFixedValues() {
		// no-op
	}

	@Override
	public void setFixedValue(final String key, final String value) {
		throw new UnsupportedOperationException("Not allowed.");
	}

	@Override
	public void setValue(final String key, final String value) {
		throw new UnsupportedOperationException("Not allowed.");
	}

	@Override
	public void setValue(final String key, final int index, final String value) {
		throw new UnsupportedOperationException("Not allowed.");
	}

	@Override
	public boolean containsKey(final String key) {
		return false;
	}

	@Override
	public boolean containsKey(final String key, final int index) {
		return false;
	}
}
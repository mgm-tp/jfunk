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
package com.mgmtp.jfunk.web.page;

import java.util.Map;

import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.web.util.WebDriverTool;

/**
 * @author rnaegele
 * @since 3.1.0
 */
public abstract class BasePage {
	private final String dataSetKey;
	protected Map<String, DataSet> dataSets;
	protected WebDriverTool wdt;

	public BasePage(final String dataSetKey, final Map<String, DataSet> dataSets, final WebDriverTool wdt) {
		this.dataSetKey = dataSetKey;
		this.dataSets = dataSets;
		this.wdt = wdt;
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

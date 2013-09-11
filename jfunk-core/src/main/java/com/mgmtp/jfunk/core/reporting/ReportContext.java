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
package com.mgmtp.jfunk.core.reporting;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * @author rnaegele
 */
public class ReportContext {

	private final String testObjectName;
	private final long startMillis;
	private long stopMillis;
	private Throwable throwable;
	private final ReportObjectType reportObjectType;
	private final Map<String, Object> reportData = newHashMapWithExpectedSize(2);

	public ReportContext(final String testObjectName, final ReportObjectType reportObjectType, final long startMillis) {
		this.testObjectName = testObjectName;
		this.reportObjectType = reportObjectType;
		this.startMillis = startMillis;
	}

	/**
	 * @return the testObjectName
	 */
	public String getTestObjectName() {
		return testObjectName;
	}

	/**
	 * @return the reportObjectType
	 */
	public ReportObjectType getReportObjectType() {
		return reportObjectType;
	}

	/**
	 * @return the startMillis
	 */
	public long getStartMillis() {
		return startMillis;
	}

	/**
	 * @return the stopMillis
	 */
	public long getStopMillis() {
		return stopMillis;
	}

	/**
	 * @param stopMillis
	 *            the stopMillis to set
	 */
	public void setStopMillis(final long stopMillis) {
		this.stopMillis = stopMillis;
	}

	public boolean isSuccess() {
		return throwable == null;
	}

	/**
	 * @return the throwable
	 */
	public Throwable getThrowable() {
		return throwable;
	}

	/**
	 * @param throwable
	 *            the throwable to set
	 */
	public void setThrowable(final Throwable throwable) {
		this.throwable = throwable;
	}

	/**
	 * Adds some data item to be used in the report.
	 * 
	 * @param key
	 *            the item key
	 * @param value
	 *            the item value
	 */
	public void putReportDataItem(final String key, final Object value) {
		reportData.put(key, value);
	}

	/**
	 * Returns the data item with the specified key.
	 * 
	 * @param key
	 *            the item key
	 * @return the data item
	 */
	public Object getReportDataItem(final String key) {
		return reportData.get(key);
	}

	/**
	 * Returns an immutable copy of the report data map.
	 * 
	 * @return the reportData
	 */
	public Map<String, Object> getReportData() {
		return ImmutableMap.copyOf(reportData);
	}
}

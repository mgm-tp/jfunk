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
import com.mgmtp.jfunk.common.util.NamedObject;

/**
 * Default {@link ReportContext} implementation.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
public class DefaultReportContext implements ReportContext {

	private String testObjectName;
	private long startMillis;
	private long stopMillis;
	private Throwable throwable;
	private Class<? extends NamedObject> testObjectType;
	private final Map<String, Object> reportData = newHashMapWithExpectedSize(2);

	@Override
	public String getTestObjectName() {
		return testObjectName;
	}

	@Override
	public void setTestObjectName(final String testObjectName) {
		this.testObjectName = testObjectName;
	}

	@Override
	public Class<? extends NamedObject> getTestObjectType() {
		return testObjectType;
	}

	@Override
	public void setTestObjectType(final Class<? extends NamedObject> testObjectType) {
		this.testObjectType = testObjectType;
	}

	@Override
	public long getStartMillis() {
		return startMillis;
	}

	@Override
	public void setStartMillis(final long startMillis) {
		this.startMillis = startMillis;
	}

	@Override
	public long getStopMillis() {
		return stopMillis;
	}

	@Override
	public void setStopMillis(final long stopMillis) {
		this.stopMillis = stopMillis;
	}

	@Override
	public boolean isSuccess() {
		return throwable == null;
	}

	@Override
	public Throwable getThrowable() {
		return throwable;
	}

	@Override
	public void setThrowable(final Throwable throwable) {
		this.throwable = throwable;
	}

	@Override
	public void putReportDataItem(final String key, final Object value) {
		reportData.put(key, value);
	}

	@Override
	public Object getReportDataItem(final String key) {
		return reportData.get(key);
	}

	@Override
	public Map<String, Object> getReportData() {
		return ImmutableMap.copyOf(reportData);
	}

	/**
	 * Does nothing.
	 */
	@Override
	public void update(final com.mgmtp.jfunk.common.util.NamedObject object) {
		// no-op
	}
}
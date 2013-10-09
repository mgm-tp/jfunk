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

import java.util.Map;

import com.mgmtp.jfunk.common.util.NamedObject;

/**
 * Encapsulates report data.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
public interface ReportContext {

	/**
	 * @return the testObjectName
	 */
	public String getTestObjectName();

	/**
	 * @param testObjectName
	 *            the testObjectName to set
	 */
	public void setTestObjectName(final String testObjectName);

	/**
	 * @return the testObjectType
	 */
	public Class<? extends NamedObject> getTestObjectType();

	/**
	 * @param testObjectType
	 *            the testObjectType to set
	 */
	public void setTestObjectType(final Class<? extends NamedObject> testObjectType);

	/**
	 * @return the startMillis
	 */
	public long getStartMillis();

	/**
	 * @param startMillis
	 *            the startMillis to set
	 */
	public void setStartMillis(final long startMillis);

	/**
	 * @return the stopMillis
	 */
	public long getStopMillis();

	/**
	 * @param stopMillis
	 *            the stopMillis to set
	 */
	public void setStopMillis(final long stopMillis);

	public boolean isSuccess();

	/**
	 * @return the throwable
	 */
	public Throwable getThrowable();

	/**
	 * @param throwable
	 *            the throwable to set
	 */
	public void setThrowable(final Throwable throwable);

	/**
	 * Adds some data item to be used in the report.
	 * 
	 * @param key
	 *            the item key
	 * @param value
	 *            the item value
	 */
	public void putReportDataItem(final String key, final Object value);

	/**
	 * Returns the data item with the specified key.
	 * 
	 * @param key
	 *            the item key
	 * @return the data item
	 */
	public Object getReportDataItem(final String key);

	/**
	 * Returns an immutable copy of the report data map.
	 * 
	 * @return the reportData
	 */
	public Map<String, Object> getReportData();

	/**
	 * Updates the report context with data from the pecified object.
	 * 
	 * @param object
	 *            the object to update the context from
	 */
	public void update(NamedObject object);
}

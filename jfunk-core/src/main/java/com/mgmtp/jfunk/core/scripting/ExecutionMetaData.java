/*
 * Copyright (c) 2014 mgm technology partners GmbH
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
package com.mgmtp.jfunk.core.scripting;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * Class for holding execution meta data.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
abstract class ExecutionMetaData {
	private Date startDate;
	private Date endDate;
	private final Map<String, Object> additionalData = new HashMap<>();
	private Throwable throwable;

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Returns an immutable copy of the internal map of additional meta data.
	 * 
	 * @return the map
	 */
	public Map<String, Object> getAdditionalData() {
		return ImmutableMap.copyOf(additionalData);
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
	 * @return the error
	 */
	public boolean isError() {
		return throwable != null;
	}

	/**
	 * Returns the value stored under the specified key in the internal map of additional meta data.
	 * For convenience, the value is internally cast to the generic type. It is the caller's
	 * responsibility to make sure the cast is safe.
	 * 
	 * @param key
	 *            the key
	 * @return the value stored under the specified key, or null
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public <T> T get(final Object key) {
		@SuppressWarnings("unchecked")
		// cast is not safe but convenient
		T value = (T) additionalData.get(key);
		return value;
	}

	/**
	 * Puts a key/value pair to the internal map of additional meta data.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @return the previous value, or null
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(final String key, final Object value) {
		return additionalData.put(key, value);
	}
}

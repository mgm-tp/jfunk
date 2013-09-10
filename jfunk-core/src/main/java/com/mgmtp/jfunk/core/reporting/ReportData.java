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

/**
 * @author rnaegele
 */
public class ReportData {

	private final String testObjectName;
	private long startMillis;
	private long stopMillis;
	private Throwable throwable;
	private final ReportDataType reportDataType;

	public ReportData(final String testObjectName, final ReportDataType reportDataType) {
		this.testObjectName = testObjectName;
		this.reportDataType = reportDataType;
	}

	/**
	 * @return the testObjectName
	 */
	public String getTestObjectName() {
		return testObjectName;
	}

	/**
	 * @return the reportDataType
	 */
	public ReportDataType getReportDataType() {
		return reportDataType;
	}

	/**
	 * @return the startMillis
	 */
	public long getStartMillis() {
		return startMillis;
	}

	/**
	 * @param startMillis
	 *            the startMillis to set
	 */
	public void setStartMillis(final long startMillis) {
		this.startMillis = startMillis;
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
}

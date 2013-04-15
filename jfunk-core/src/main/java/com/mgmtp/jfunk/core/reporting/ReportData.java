/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.reporting;

import com.mgmtp.jfunk.common.util.NamedObject;

/**
 * @author rnaegele
 */
public class ReportData {

	private final NamedObject testObject;
	private long startMillis;
	private long stopMillis;
	private Throwable throwable;

	public ReportData(final NamedObject testObject) {
		this.testObject = testObject;
	}

	/**
	 * @return the testObject
	 */
	public NamedObject getTestObject() {
		return testObject;
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

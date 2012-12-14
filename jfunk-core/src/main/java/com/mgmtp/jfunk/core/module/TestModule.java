/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.module;

import com.mgmtp.jfunk.common.util.NamedObject;

/**
 * A test module can be seen as some kind of container that encapsulates test logic. The test logic
 * is run in its {@link #execute()} method.
 * 
 * @author rnaegele
 * @version $Id$
 */
public interface TestModule extends NamedObject {

	/**
	 * Executes the test module.
	 */
	void execute();

	/**
	 * @return the data set key
	 */
	String getDataSetKey();

	/**
	 * @param dataSetKey
	 *            the data set key
	 */
	void setDataSetKey(String dataSetKey);

	@Deprecated
	String getVerfahren();

	/**
	 * Sets an error flag.
	 * 
	 * @param error
	 *            the error
	 */
	void setError(boolean error);

	/**
	 * Gets the error flag.
	 * 
	 * @return the error
	 */
	boolean isError();
}

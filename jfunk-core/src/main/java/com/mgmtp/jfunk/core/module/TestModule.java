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
package com.mgmtp.jfunk.core.module;

import com.mgmtp.jfunk.common.util.NamedObject;

/**
 * A test module can be seen as some kind of container that encapsulates test logic. The test logic
 * is run in its {@link #execute()} method.
 * 
 * @author rnaegele
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

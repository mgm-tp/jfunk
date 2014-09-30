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
package com.mgmtp.jfunk.core.step.base;

/**
 * Enum for step modes. {@link Step}s may use this in order to determine whether e. g. to set or to
 * check values.
 * 
 */
public enum StepMode {

	/**
	 * Step mode with the intention that nothing should be done.
	 */
	NONE,

	/**
	 * Step mode with the intention that values should be checked.
	 */
	CHECK_VALUE,

	/**
	 * Step mode with the intention that default values should be checked.
	 */
	CHECK_DEFAULT,

	/**
	 * Step mode with the intention that values should be set.
	 */
	SET_VALUE,
	/**
	 * Editiert den Wert des Parameters und setzt ihn auf den entsprechenden FormData Wert
	 */
}

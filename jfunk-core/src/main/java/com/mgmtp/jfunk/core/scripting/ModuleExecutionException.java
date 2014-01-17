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

import com.mgmtp.jfunk.core.module.TestModule;

/**
 * Exceptions thrown during test module execution are wrapped into a
 * {@link ModuleExecutionException}.
 * 
 * @author rnaegele
 */
public class ModuleExecutionException extends RuntimeException {

	private final TestModule module;

	/**
	 * Creates a new instance for the specified test module and cause.
	 * 
	 * @param module
	 *            the test module whose execution resulted in an exception
	 * @param cause
	 *            the cause
	 */
	public ModuleExecutionException(final TestModule module, final Throwable cause) {
		super(cause);
		this.module = module;
	}

	/**
	 * @return the module
	 */
	public TestModule getModule() {
		return module;
	}
}

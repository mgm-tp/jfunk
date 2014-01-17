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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base implementation for steps.
 * 
 */
public abstract class BaseStep implements Step {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected final String name;

	/**
	 * Creates a new instance.
	 * 
	 * @param name
	 *            the step's name (if {@code null}, {@code getClass().getSimpleName()} is used)
	 */
	public BaseStep(final String name) {
		this.name = name == null ? getClass().getSimpleName() : name;
	}

	/**
	 * Creates a new instance with the return value of {@code getClass().getSimpleName()} as its
	 * name.
	 */
	public BaseStep() {
		this(null);
	}

	@Override
	public String getName() {
		return name;
	}
}
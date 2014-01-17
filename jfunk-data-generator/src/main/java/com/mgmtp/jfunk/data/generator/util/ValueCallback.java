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
package com.mgmtp.jfunk.data.generator.util;

import com.mgmtp.jfunk.data.generator.constraint.Constraint;

/**
 * Used by a {@link Constraint} during initialisation to determine if a fixed value is set. If a
 * fixed value is set no other value will be generated until
 * {@link Constraint#setValueCallback(ValueCallback)} is set to {@code null}. A fixed value survives
 * {@link Constraint#resetValues()}.
 * 
 */
public interface ValueCallback {

	/**
	 * @return a fixed value or {@code null} if no fixed value is set.
	 */
	String getImmutableValue();
}
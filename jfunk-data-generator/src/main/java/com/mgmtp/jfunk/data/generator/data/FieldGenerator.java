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
package com.mgmtp.jfunk.data.generator.data;

/**
 * Classes used for manipulating {@link Field Fields} in {@link FieldSet FieldSets} after the
 * generation must implement this interface and are then called with the so far generated
 * {@link FormData}.
 * 
 */
public interface FieldGenerator {
	/**
	 * Called when the {@link FieldSet} the {@link Field} belongs to is generated completely.
	 * 
	 * @param formData
	 *            the so far generated {@link FormData}
	 */
	void generate(FormData formData);
}
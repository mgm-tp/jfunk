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
package com.mgmtp.jfunk.data.generator;

import com.mgmtp.jfunk.data.generator.data.IndexedFields;

/**
 * jFunk data generator specific constants.
 * 
 */
public class GeneratorConstants {
	/**
	 * The value belonging to this key is the name of the generator configuration file.
	 */
	public static final String GENERATOR_CONFIG_FILE = "dataSource.generator.configFile";

	/**
	 * The value belonging to this key is the name of generator configuration file containing the
	 * definitions for all indexed fields. See {@link IndexedFields} for more information concerning
	 * this topic.
	 */
	public static final String INDEXED_FIELDS_FILE = "dataSource.generator.indexedFieldsFile";

	/**
	 * Key for global constraints which are available in all underlying constraint groups.
	 */
	public static final String ALL_CONSTRAINT = "all";

	private GeneratorConstants() {
		// don't allow instantiation
	}
}
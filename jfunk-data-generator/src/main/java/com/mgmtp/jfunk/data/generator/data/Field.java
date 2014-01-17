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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.mgmtp.jfunk.data.generator.GeneratorConstants;

/**
 * Represents a {@code field} element included in a {@code field-set} element in the
 * "indexed-fields" configuration file (defined by {@link GeneratorConstants#INDEXED_FIELDS_FILE}).
 * 
 */
class Field {

	private final String dataKey;
	private final String entryKey;
	private final boolean unique;
	private final String className;

	public Field(final String id, final boolean unique, final String className) {
		this.dataKey = StringUtils.substringBefore(id, ".");
		this.entryKey = StringUtils.substringAfter(id, ".");
		this.unique = unique;
		this.className = className;
	}

	public String getDataKey() {
		return dataKey;
	}

	public String getEntryKey() {
		return entryKey;
	}

	public boolean isUnique() {
		return unique;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
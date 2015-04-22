/*
 * Copyright (c) 2015 mgm technology partners GmbH
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

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.mgmtp.jfunk.data.generator.GeneratorConstants;

/**
 * Represents a {@code field-set} element in the "indexed-fields" configuration file (defined by
 * {@link GeneratorConstants#INDEXED_FIELDS_FILE}).
 * 
 */
class FieldSet implements Iterable<Field> {
	private final String id;
	private final String dataKey;
	private final String dependsOn;
	private final Set<Field> fields = Sets.newLinkedHashSet();
	private final Set<String> dependencies = Sets.newHashSet();

	public FieldSet(final String id, final String dataKey, final String dependsOn) {
		this.id = id;
		this.dataKey = dataKey;
		this.dependsOn = dependsOn;
	}

	public void addField(final Field field) {
		fields.add(field);
		if (!field.getDataKey().equals(dataKey)) {
			dependencies.add(field.getDataKey());
		}
	}

	public String getId() {
		return id;
	}

	public String getDataKey() {
		return dataKey;
	}

	public String getDependsOn() {
		return dependsOn;
	}

	public Set<String> getDependencies() {
		return Collections.unmodifiableSet(dependencies);
	}

	@Override
	public Iterator<Field> iterator() {
		return Iterators.unmodifiableIterator(fields.iterator());
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
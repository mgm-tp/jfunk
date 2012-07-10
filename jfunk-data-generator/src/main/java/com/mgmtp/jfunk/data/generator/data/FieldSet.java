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
 * @version $Id$
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
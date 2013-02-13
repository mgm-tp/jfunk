/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
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
 * @version $Id$
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
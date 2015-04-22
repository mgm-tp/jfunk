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

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.GeneratorConstants;
import com.mgmtp.jfunk.data.generator.constraint.Constraint;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.exception.IdNotFoundException;

/**
 * Encapsulates the form data for a certain form. This data can be generated either partially or
 * completely.
 * 
 */
public class FormData {

	private final Logger log = Logger.getLogger(getClass());

	private final String key;
	private final Map<String, FormEntry> entries = Maps.newHashMap();
	private final Generator generator;
	private final Configuration configuration;
	private final Multimap<String, String> uniqueValuesMap = HashMultimap.create();
	private final Map<String, Map<String, String>> fixedValuesMap;

	private final Map<String, FieldGenerator> fieldGenerators;

	/**
	 * Creates a new instance for form data
	 * 
	 * @param key
	 *            the name of the form or the prefix for the constraint IDs in the generator
	 * @param generator
	 *            the generator object
	 */
	@Inject
	public FormData(final Configuration configuration, final Map<String, FieldGenerator> fieldGenerators, @Assisted final String key,
			@Assisted final Generator generator, @Assisted final Map<String, Map<String, String>> fixedValuesMap) {
		this.configuration = configuration;
		this.fieldGenerators = fieldGenerators;
		this.key = key;
		this.generator = generator;
		this.fixedValuesMap = fixedValuesMap;
	}

	public boolean generate() throws IOException {
		//First set fixed values. This has to be done first so that they will be
		//considered during generation.
		Map<String, String> fixedValues = fixedValuesMap.get(key);
		if (fixedValues != null) {
			for (Map.Entry<String, String> entry : fixedValues.entrySet()) {
				addFormEntry(entry.getKey(), entry.getValue());
			}
		}

		Constraint constraintContainer = null;
		try {
			constraintContainer = generator.getConstraint(key + "." + GeneratorConstants.ALL_CONSTRAINT);
		} catch (IdNotFoundException e) {
			log.info("No constraint-container found for id " + key);
			return false;
		}

		//get all constraints contained in the FormEntry Map
		String keyPrefix = key + ".";
		String allKeyPrefix = GeneratorConstants.ALL_CONSTRAINT + ".";
		String excludedKey = key + "." + GeneratorConstants.ALL_CONSTRAINT;
		Set<String> ids = constraintContainer.getContainedIds();

		//Reset to generate new values
		resetValues(constraintContainer);
		generateValues(constraintContainer);

		//First of all, all unindexed entries.
		for (String id : ids) {
			if (id.equals(excludedKey)) {
				continue;
			}
			String prefix = id.startsWith(keyPrefix) ? keyPrefix : allKeyPrefix;
			// TODO some parts may get cut off here
			String entryKey = id.substring(prefix.length());

			if (fixedValues != null && fixedValues.containsKey(entryKey)) {
				continue;
			}
			getFormEntry(entryKey);
		}

		//Only indexed values; this way it is ensured, that fields, which represent
		//the number of lines, have already been generated.
		Set<FieldSet> indexedFields = generator.getIndexedFields().getFieldSets(key);

		Map<String, FieldSet> fieldSetCache = Maps.newHashMap();
		for (FieldSet fieldSet : indexedFields) {
			String id = fieldSet.getId();
			fieldSetCache.put(id, fieldSet);
			String dependsOn = fieldSet.getDependsOn();
			/*
			 * If a dependency to another FieldSet exists, it has to be in the cache already (order
			 * in the xml!)
			 */
			FieldSet dependentFieldSet = StringUtils.isNotEmpty(dependsOn) ? fieldSetCache.get(dependsOn) : null;
			if (StringUtils.isNotEmpty(dependsOn) && dependentFieldSet == null) {
				throw new IllegalStateException("The FieldSet with id=" + id + " depends on the FieldSet with id=" + dependsOn
						+ ". However, this hasn't been generated yet. Is the order within the Generator-XML correct?");
			}

			int count;
			if (id.startsWith(key)) {
				//The number of indexes is read from a FormEntry.
				try {
					count = getFormEntry(StringUtils.substringAfter(id, ".")).getInteger();
				} catch (NumberFormatException ex) {
					// Not defined for this year, so return 0 lines.
					count = 0;
				}
			} else {
				// The number of indexes is configured as a property.
				count = configuration.getInteger(id, 5);
			}

			Map<String, FormData> dependenciesMap = Maps.newHashMap();

			for (int i = 1; i <= count; ++i) {
				for (String dependency : fieldSet.getDependencies()) {
					FormData depData = new FormData(configuration, fieldGenerators, dependency, generator, fixedValuesMap);
					depData.generate();
					dependenciesMap.put(dependency, depData);
				}

				//Index entries. All entries for one index are generated in a loop so as to
				//consider dependencies between entries.
				for (Field field : fieldSet) {
					FormData data = key.equals(field.getDataKey()) ? this : dependenciesMap.get(field.getDataKey());

					//Index FormEntry
					if (!indexFormEntry(data, field, i, fixedValues)) {
						//Not unique, try this index again.
						--i;
						break;
					}
				}
				//Reset entries for next run when all values for an index have been generated.
				for (Field field : fieldSet) {
					if (!key.equals(field.getDataKey())) {
						//Not for dependencies, since those are newly generated for each index anyway.
						continue;
					}

					FormEntry fe = getFormEntry(field.getEntryKey());
					fe.resetValue();
					Constraint constraint = fe.getConstraint();
					if (constraint != null) {
						constraint.resetValues();
					}
				}
			}
			for (Field field : fieldSet) {
				String className = field.getClassName();
				if (StringUtils.isNotEmpty(className)) {
					FieldGenerator fieldGenerator = fieldGenerators.get(className);
					fieldGenerator.generate(this);
				}
			}
		}
		return true;
	}

	Map<String, FormEntry> getEntries() {
		return entries;
	}

	/**
	 * Returns the FormEntry for the given entryKey and index. This is the same as calling
	 * getFormEntry(entryKey+index)
	 * 
	 * @see #getFormEntry(String)
	 */
	public FormEntry getFormEntry(final String entryKey, final int index) {
		return getFormEntry(entryKey + JFunkConstants.INDEXED_KEY_SEPARATOR + index);
	}

	/**
	 * Returns true if the FormEntry exists in the table
	 * 
	 * @return true, if the key exists in the FormEntry table
	 */
	public boolean hasFormEntry(final String entryKey) {
		return entries.containsKey(entryKey);
	}

	/**
	 * Returns the FormEntry for the given key. If no FormEntry exists yet for the given key, then a
	 * new one will be generated if generate == true
	 * 
	 * @return the FormEntry for the key
	 */
	public FormEntry getFormEntry(final String entryKey) {
		FormEntry entry = entries.get(entryKey);
		if (entry == null) {
			entry = addFormEntry(entryKey, null);
		}
		return entry;
	}

	private FormEntry addFormEntry(final String entryKey, final String fixedValue) {
		FormEntry entry = null;
		Constraint constraint = null;
		try {
			constraint = generator.getConstraint(key + "." + entryKey);
		} catch (IdNotFoundException e) {
			// ignore
			try {
				constraint = generator.getConstraint(GeneratorConstants.ALL_CONSTRAINT + "." + entryKey);
			} catch (IdNotFoundException e1) {
				// ignore
			}
		}
		entry = new FormEntry(key, entryKey, fixedValue, constraint, configuration);
		entries.put(entryKey, entry);
		return entry;
	}

	/**
	 * Resets all {@link FormEntry} objects in this object and calls
	 * {@link Constraint#initValues(FieldCase)} on the constraintContainer if
	 * <code>generateMissingEntries == true</code>. That call initializes all constraints if they
	 * have not yet been initialized. If a {@link FormEntry} contains a fixed value it is not
	 * changed. To generate new values, {@code #resetValues(Constraint)} has to be called first.
	 * This method does not reset the generated value to {@code null}.
	 */
	private void generateValues(final Constraint constraintContainer) {
		log.info("Generate previously reset values for form data " + key + ". Values that have not been reset are kept.");
		constraintContainer.initValues(null);
	}

	/**
	 * Calls the reset-method on all FormEntry-objects and the constraint-container. This resets all
	 * FormEntry-values to the standard value, if there is one, and prepares the generator for a new
	 * initialization.
	 */
	private void resetValues(final Constraint constraintContainer) {
		log.info("Reset " + key + " form data");
		for (FormEntry e : entries.values()) {
			e.resetValue();
		}
		constraintContainer.resetValues();
	}

	/**
	 * Sets the {@link FormEntry} for {@code key+index} to the value of the {@link FormEntry} for
	 * {@code key}. This method can be used for several lines within the same basic data set.
	 * 
	 * @return true if a value could be generated, false if the value already existed (but cannot be
	 *         regenerated because of it having to be unique)
	 */
	private boolean indexFormEntry(final FormData formData, final Field field, final int index, final Map<String, String> fixedValues) {
		String entryKey = field.getEntryKey();
		FormEntry entry = formData.getFormEntry(entryKey);
		boolean unique = field.isUnique();
		String value = entry.getValue();

		String indexedKey = entryKey + JFunkConstants.INDEXED_KEY_SEPARATOR + index;
		if (fixedValues != null && fixedValues.containsKey(indexedKey)) {
			return true;
		}

		if (unique && !uniqueValuesMap.put(entry.getKey(), value)) {
			log.debug("Value for " + entryKey + " has already been generated, but has to be unique.");
			return false;
		}

		FormEntry target = getFormEntry(indexedKey);
		target.setCurrentValue(value);
		return true;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		tsb.append("key", key);
		tsb.append("entries", entries);
		tsb.append("fixedValuesMap", fixedValuesMap);
		return tsb.toString();
	}
}
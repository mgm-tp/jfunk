/*
 * Copyright (c) 2013 mgm technology partners GmbH
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.config.ScriptScoped;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.common.util.ResourceLoader;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.data.DefaultDataSet;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.GeneratorConstants;
import com.mgmtp.jfunk.data.generator.constraint.Constraint;
import com.mgmtp.jfunk.data.generator.constraint.base.BaseConstraint;
import com.mgmtp.jfunk.data.source.BaseDataSource;
import com.mgmtp.jfunk.data.source.DataSource;

/**
 * {@link DataSource} implementation for the online generator (= classic generator).
 * 
 */
@ScriptScoped
public final class GeneratorDataSource extends BaseDataSource {

	public static final Logger LOG = Logger.getLogger(GeneratorDataSource.class);

	private Generator generator;
	private final FormDataFactory formDataFactory;
	private final Provider<Generator> generatorProvider;
	private final Set<String> dataSetKeys = Sets.newHashSet();

	@Inject
	public GeneratorDataSource(final Configuration configuration, final FormDataFactory formDataFactory, final Provider<Generator> generatorProvider) {
		super(configuration);
		this.formDataFactory = formDataFactory;
		this.generatorProvider = generatorProvider;
	}

	@Override
	protected DataSet getNextDataSetImpl(final String key) {
		try {
			/*
			 * Reset the value callback in the constraints so we don't remember some old value from
			 * a previous run. If we do want a fixed value it is handled in the DataSet.
			 */
			Generator gen = getGenerator();
			Collection<String> ids = gen.getConstraintIds();
			int counter = 0;
			for (String id : ids) {
				if (id.startsWith(key)) {
					counter++;
					Constraint constraint = gen.getConstraint(id);
					if (constraint instanceof BaseConstraint) {
						BaseConstraint bc = (BaseConstraint) constraint;
						bc.setValueCallback(null);
					}
				}
			}
			if (counter == 1) {
				LOG.warn("The constraint " + key
						+ ".all did not contain any subconstraints so no data will be generated. Please check your generator configuration.");
			}
			gen.setTestmode(StringUtils.isNotEmpty(configuration.get(JFunkConstants.TESTMERKER)));
			getFormDataKeys(); // Makes sure fixed values are loaded
			FormData data = formDataFactory.create(key, gen, fixedValues);

			DataSet ds = new DefaultDataSet();
			if (data.generate()) {
				Map<String, FormEntry> entries = data.getEntries();

				for (FormEntry entry : entries.values()) {
					ds.setValue(entry.getKey(), entry.getValue());
				}
				/*
				 * TODO Disabled as it causes problems in ELSTER. Will be fixed in December 2011.
				 * See MQA-745 for details.
				 */
				//				cleanUp(ds);
			}
			return ds;
		} catch (IOException ex) {
			throw new IllegalStateException("Error creating form data: " + key, ex);
		}
	}

	/**
	 * Check DataSet for indexed entries and remove all keys without index if indexed entries with
	 * the same name exist.
	 */
	private void cleanUp(final DataSet ds) {
		Set<String> indexedKeys = Sets.newHashSet();
		for (String key : ds.getDataView().keySet()) {
			// Search for indexed entries
			if (key.contains("#")) {
				// Remember key (without index)
				indexedKeys.add(key.substring(0, key.indexOf("#")));
			}
		}
		// Remove all indexes
		for (String indexedKey : indexedKeys) {
			ds.removeValue(indexedKey);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return Always returns {@code true} for known data set keys, because new data is generated
	 *         upon request.
	 */
	@Override
	public boolean hasMoreData(final String dataSetKey) {
		if (dataSetKeys.isEmpty()) {
			// Fill set of known data set keys
			for (String constraintId : getGenerator().getConstraintIds()) {
				if (constraintId != null && constraintId.endsWith(".all")) {
					String key = StringUtils.substringBefore(constraintId, "." + GeneratorConstants.ALL_CONSTRAINT);
					dataSetKeys.add(key);
				}
			}
		}
		return dataSetKeys.contains(dataSetKey);
	}

	public Generator getGenerator() {
		if (generator == null) {
			String seedString = configuration.get(JFunkConstants.RANDOM_SEED, false);
			Long seed = seedString == null ? null : Long.valueOf(seedString);
			GeneratorState state = GeneratorState.currentState();
			state.initMathRandom(seed);

			String indexedFieldsFile = configuration.get(GeneratorConstants.INDEXED_FIELDS_FILE);

			generator = generatorProvider.get();

			try {
				// Load configuration for indexed fields
				SAXBuilder builder = new SAXBuilder();
				IndexedFields indexedFields = new IndexedFields();

				if (indexedFieldsFile != null) {
					Document doc = builder.build(ResourceLoader.getConfigInputStream(indexedFieldsFile));

					@SuppressWarnings("unchecked")
					List<Element> elements = doc.getRootElement().getChildren("field-set");
					for (Element fieldSetElem : elements) {
						String id = fieldSetElem.getAttributeValue("id");
						String dataKey = fieldSetElem.getAttributeValue("dataKey");
						String dependsOn = fieldSetElem.getAttributeValue("dependsOn", "");
						FieldSet fieldSet = new FieldSet(id, dataKey, dependsOn);
						indexedFields.addFieldSet(fieldSet);

						@SuppressWarnings("unchecked")
						List<Element> fieldElems = fieldSetElem.getChildren("field");
						for (Element fieldElem : fieldElems) {
							String fieldId = fieldElem.getAttributeValue("id");
							boolean unique = Boolean.parseBoolean(fieldElem.getAttributeValue("unique"));
							String className = fieldElem.getAttributeValue("class", "");
							Field field = new Field(fieldId, unique, className);
							fieldSet.addField(field);
						}
					}
				}
				generator.parseXml(indexedFields);
			} catch (Exception ex) {
				throw new IllegalStateException("Error initializing generator.", ex);
			}
		}
		return generator;
	}

	@Override
	public void doReset() {
		dataSetKeys.clear();
	}
}
/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.common.util.ResourceLoader;
import com.mgmtp.jfunk.data.generator.constraint.Constraint;
import com.mgmtp.jfunk.data.generator.constraint.ConstraintFactory;
import com.mgmtp.jfunk.data.generator.control.ControlFactory;
import com.mgmtp.jfunk.data.generator.data.IndexedFields;
import com.mgmtp.jfunk.data.generator.exception.IdNotFoundException;
import com.mgmtp.jfunk.data.generator.field.FieldFactory;
import com.mgmtp.jfunk.data.generator.util.CharacterSet;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * The jFunk data generator provides an easy way to generate data which can then be used to fill web
 * forms. The data generation is performed on-the-fly during the test execution. This class is the
 * starting point for generating data. The method {@link #parseXml(IndexedFields)} reads a XML based
 * configuration file and initializes all constraint- and field objects. Constraint objects can be
 * obtained using {@link #getConstraint(String)}. All available constraints can be found in the
 * subpackage {@code constraint}.
 * 
 * @version $Id$
 */
public class Generator {

	public static final Logger LOGGER = Logger.getLogger(Generator.class);

	private final FieldFactory fieldFactory;
	private final ConstraintFactory constraintFactory;
	private final ControlFactory controlFactory;
	private Collection<Constraint> constraints;

	/**
	 * Counter to prevent endless recursion while creating data
	 */
	private long token = 0;
	private final MathRandom random;
	private final boolean ignoreOptionalConstraints;
	private boolean testmode;
	private final Configuration configuration;

	private IndexedFields indexedFields;

	public Generator(final MathRandom random, final boolean ignoreOptionalConstraints, final Configuration configuration,
			final Injector injector) {
		this.random = random;
		this.ignoreOptionalConstraints = ignoreOptionalConstraints;
		this.constraintFactory = new ConstraintFactory(this, injector);
		this.fieldFactory = new FieldFactory();
		this.controlFactory = new ControlFactory();
		this.configuration = configuration;
	}

	public void parseXml(final IndexedFields theIndexedFields) throws IOException, JDOMException {
		this.indexedFields = theIndexedFields;
		final String generatorFile = configuration.get(GeneratorConstants.GENERATOR_CONFIG_FILE);
		if (StringUtils.isBlank(generatorFile)) {
			LOGGER.info("No generator configuration file found");
			return;
		}
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(true);
		builder.setIgnoringElementContentWhitespace(false);
		builder.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
		builder.setEntityResolver(new EntityResolver() {
			@Override
			public InputSource resolveEntity(final String publicId, final String systemId) throws IOException {
				int index = systemId.lastIndexOf('/');
				if (index == -1) {
					index = 0;
				}
				String filename = configuration.processPropertyValue(systemId.substring(index));
				File subFile = new File(filename);
				InputStream in = null;
				if (!subFile.exists()) {
					subFile = new File(new File(generatorFile).getParent(), filename);
				}
				try {
					if (subFile.isAbsolute()) {
						in = ResourceLoader.getInputStream(subFile.getPath());
					} else {
						in = ResourceLoader.getConfigInputStream(subFile.getPath());
					}
				} catch (Exception e) {
					// ignore
					in = null;
				}
				if (in == null) {
					throw new IllegalStateException("Error reading generator configuration; could not find entity " + systemId);
				}
				return new InputSource(new InputStreamReader(in));
			}
		});

		InputStream in = ResourceLoader.getConfigInputStream(generatorFile);
		Document doc = null;
		try {
			doc = builder.build(in);
			Element root = doc.getRootElement();

			Element charsetsElement = root.getChild(XMLTags.CHARSETS);
			@SuppressWarnings("unchecked")
			List<Element> charsetElements = charsetsElement.getChildren(XMLTags.CHARSET);
			for (Element element : charsetElements) {
				CharacterSet.initCharacterSet(element);
			}

			@SuppressWarnings("unchecked")
			List<Element> constraintElements = root.getChild(XMLTags.CONSTRAINTS).getChildren(XMLTags.CONSTRAINT);
			constraints = Lists.newArrayListWithExpectedSize(constraintElements.size());
			for (Element element : constraintElements) {
				constraints.add(constraintFactory.createModel(random, element));
			}
		} finally {
			closeQuietly(in);
		}

		LOGGER.info("Generator was successfully initialized");
	}

	public IndexedFields getIndexedFields() {
		return indexedFields;
	}

	public ConstraintFactory getConstraintFactory() {
		return constraintFactory;
	}

	public FieldFactory getFieldFactory() {
		return fieldFactory;
	}

	public ControlFactory getControlFactory() {
		return controlFactory;
	}

	/**
	 * Returns the constraint whose id matches the given key
	 */
	public Constraint getConstraint(final String key) throws IdNotFoundException {
		return constraintFactory.getModel(key);
	}

	/**
	 * Returns a list of all constraint IDs contained in this generator
	 * 
	 * @return all constraint IDs
	 */
	public Collection<String> getConstraintIds() {
		return constraintFactory.getModelIds();
	}

	public long getCurrentToken() {
		return token++;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public boolean isIgnoreOptionalConstraints() {
		return ignoreOptionalConstraints;
	}

	/**
	 * The generator can be run in test mode which can e.g. result in different data being generated
	 * by the underlying (user defined) constraints. However, it depends on the underlying
	 * constraints if it makes sense to switch to test mode. The standard jFunk constraints do not
	 * differentiate between real and test mode.
	 * 
	 * @return {@code true} when generator runs in test mode, {@code false} otherwise
	 */
	public boolean isTestmode() {
		return testmode;
	}

	/**
	 * Activates or deactivates the test mode. See {@link #isTestmode()} for an explanation of test
	 * mode.
	 * 
	 * @param testmode
	 *            {@code true} to activate the test mode, {@code false} to deactivate the test mode
	 */
	public void setTestmode(final boolean testmode) {
		this.testmode = testmode;
	}

	/**
	 * This method lists only top level constraints, e.g. login.all. To get all constraints use
	 * {@link #getConstraintIds} to get all available IDs and then {@link #getConstraint(String)} to
	 * retrieve the single constraint.
	 * 
	 * @return a list of constraints
	 */
	public Collection<Constraint> getConstraints() {
		return constraints;
	}
}
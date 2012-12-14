/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.inject.Module;
import com.mgmtp.jfunk.common.util.ResourceLoader;

/**
 * Utility class for loading Guice modules defined as properties. Automatically adds
 * {@link JFunkDefaultModule} to the list of modules. Further modules may be specified in a
 * properties file. By default, a file named "jfunk.properties" is loaded. Another file may be
 * specified with the system property "jfunk.props.file." Modules may be specified as follows:
 * 
 * <pre>
 * module.1=com.foo.bar.config.MyFirstAdditionalGuiceModule
 * module.2=com.foo.bar.config.MyVerySpecialGuiceModule
 * </pre>
 * 
 * @version $Id$
 */
public final class ModulesLoader {
	private static final Logger LOG = Logger.getLogger(ModulesLoader.class);

	private ModulesLoader() {
		// nothing to do
	}

	/**
	 * Loads Guice modules whose class names are specified as properties. All properties starting
	 * with "module." are considered to have a fully qualified class name representing a Guice
	 * module.
	 * 
	 * @param propertiesFile
	 *            The properties file
	 * @return A list of Guice modules
	 */
	public static List<Module> loadModulesFromProperties(final Module jFunkModule, final String propertiesFile)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		final List<Module> modules = Lists.newArrayList();
		modules.add(jFunkModule);

		LOG.debug("Using jfunk.props.file=" + propertiesFile);
		Properties props = loadProperties(propertiesFile);

		for (final Enumeration<?> en = props.propertyNames(); en.hasMoreElements();) {
			String name = (String) en.nextElement();
			if (name.startsWith("module.")) {
				String className = props.getProperty(name);
				LOG.info("Loading " + name + "=" + className);
				Class<? extends Module> moduleClass = Class.forName(className).asSubclass(Module.class);
				Module module = moduleClass.newInstance();
				modules.add(module);
			}
		}
		return modules;
	}

	private static Properties loadProperties(final String propsFileName) throws IOException {
		Properties props = new Properties();
		InputStream is = null;
		try {
			is = ResourceLoader.getConfigInputStream(propsFileName);
			props.load(is);
		} finally {
			IOUtils.closeQuietly(is);
		}
		return props;
	}
}
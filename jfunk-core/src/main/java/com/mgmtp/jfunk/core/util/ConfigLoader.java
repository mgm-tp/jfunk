/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.util;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.util.Configuration;

/**
 * Utility class for loading the configuration.
 * 
 * @author rnaegele
 * @version $Id$
 */
public final class ConfigLoader {
	private static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);

	private ConfigLoader() {
		// don't allow instantiation
	}

	/**
	 * Loads the configuration into the specified {@link Configuration} object.
	 * 
	 * @param config
	 *            the config object
	 * @param fileName
	 *            the config file name
	 * @param preserveExistingProps
	 *            {@code true}, if already existing properties in the specified config objects are
	 *            to be preserved
	 * @param globalProps
	 *            {@code true}, if global properties should be loaded (global properties are set as
	 *            system properties)
	 */
	public static void loadConfig(final Configuration config, final String fileName, final boolean preserveExistingProps, final boolean globalProps) {
		LOG.info("Loading config file: " + fileName + " (preserveExistingProps=" + preserveExistingProps + ")");

		config.load(fileName, preserveExistingProps);

		/*
		 * Extra handling for system properties starting with JFunkConstants#SYSTEM_PROPERTIES: they
		 * will be loaded as property files.
		 */
		for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
			String key = (String) entry.getKey();
			if (key.startsWith(JFunkConstants.SYSTEM_PROPERTIES)) {
				String value = (String) entry.getValue();
				LOG.debug("Found system property " + key + ". Its value=" + value + " will be loaded as a property file");
				config.put(key, value);
			}
		}

		if (globalProps) {
			Properties props = System.getProperties();
			copyProperty(props, config, JFunkConstants.JAVAX_NET_SSL_KEY_STORE_PASSWORD);
			copyProperty(props, config, JFunkConstants.JAVAX_NET_SSL_KEY_STORE);
			copyProperty(props, config, JFunkConstants.JAVAX_NET_SSL_KEY_STORE_TYPE);
			copyProperty(props, config, JFunkConstants.JAVAX_NET_SSL_TRUST_STORE_PASSWORD);
			copyProperty(props, config, JFunkConstants.JAVAX_NET_SSL_TRUST_STORE);
			copyProperty(props, config, JFunkConstants.JAVAX_NET_SSL_TRUST_STORE_TYPE);
		}

		// Archiving is always necessary when execution mode is 'start'.
		// Otherwise continuing after the breakpoint would not be possible.
		if (JFunkConstants.EXECUTION_MODE_START.equals(config.get(JFunkConstants.EXECUTION_MODE))) {
			config.put(JFunkConstants.ARCHIVING_MODE, JFunkConstants.ARCHIVING_MODE_ALL);
		}
	}

	private static void copyProperty(final Properties target, final Configuration source, final String key) {
		String value = source.get(key);
		if (!Strings.isNullOrEmpty(value)) {
			target.setProperty(key, value);
		}
	}
}

/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.config;

import static com.google.common.base.Preconditions.checkState;
import static com.mgmtp.jfunk.common.util.Varargs.va;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.MembersInjector;
import com.mgmtp.jfunk.common.util.Configuration;

/**
 * {@link MembersInjector} for injecting configuration values.
 * 
 * @author rnaegele
 * @version $Id$
 * @param <T>
 *            the type of the instance subject to injection
 * @see InjectConfig
 */
public class ConfigurationMembersInjector<T> implements MembersInjector<T> {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Field field;
	private final Provider<Configuration> configurationProvider;

	public ConfigurationMembersInjector(final Field field, final Provider<Configuration> configurationProvider) {
		this.field = field;
		this.configurationProvider = configurationProvider;
	}

	@Override
	public void injectMembers(final T instance) {
		InjectConfig injectConfigAnnotation = field.getAnnotation(InjectConfig.class);

		String configKey = injectConfigAnnotation.name();
		String defaultValue = injectConfigAnnotation.defaultValue();

		boolean hasDefault = !defaultValue.equals(InjectConfig.NO_DEFAULT);

		Configuration configuration = configurationProvider.get();
		boolean containsKey = configuration.containsKey(configKey);

		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			@Override
			public Void run() {
				field.setAccessible(true);
				return null;
			}
		});

		try {
			Class<?> type = field.getType();
			if (type == Integer.TYPE) {
				int value;
				if (containsKey) {
					value = configuration.getInteger(configKey);
				} else {
					checkDefaultValue(configKey, hasDefault);
					value = Integer.parseInt(defaultValue);
				}
				field.setInt(instance, value);
				logInjection(configKey, value, defaultValue);
			} else if (type == Boolean.TYPE) {
				boolean value;
				if (containsKey) {
					value = configuration.getBoolean(configKey);
				} else {
					checkDefaultValue(configKey, hasDefault);
					value = Boolean.parseBoolean(defaultValue);
				}
				field.setBoolean(instance, value);
				logInjection(configKey, value, defaultValue);
			} else if (type == Long.TYPE) {
				long value;
				if (containsKey) {
					value = configuration.getLong(configKey);
				} else {
					checkDefaultValue(configKey, hasDefault);
					value = Long.parseLong(defaultValue);
				}
				field.setLong(instance, value);
				logInjection(configKey, value, defaultValue);
			} else {
				String value;
				if (containsKey) {
					value = configuration.get(configKey);
				} else {
					checkDefaultValue(configKey, hasDefault);
					value = defaultValue;
				}
				field.set(instance, value);
				logInjection(configKey, value, defaultValue);
			}
		} catch (IllegalAccessException ex) {
			throw new IllegalStateException("Error injecting configuration item '" + configKey + "'", ex);
		}
	}

	private void logInjection(final String configKey, final Object value, final String defaultValue) {
		log.debug("Injected config property: name={}, value={}, defaultValue={}", va(configKey, value, defaultValue));
	}

	private void checkDefaultValue(final String configKey, final boolean hasDefault) {
		checkState(hasDefault, "Key '" + configKey + "' not found in configuration and no default value specified.");
	}
}
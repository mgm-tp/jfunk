/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.config;

import java.lang.reflect.Field;

import javax.inject.Provider;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.mgmtp.jfunk.common.util.Configuration;

/**
 * 
 * @author rnaegele
 * @version $Id$
 */
public class ConfigurationTypeListener implements TypeListener {

	private final Provider<Configuration> configurationProvider;

	@Inject
	public ConfigurationTypeListener(final Provider<Configuration> configurationProvider) {
		this.configurationProvider = configurationProvider;
	}

	@Override
	public <T> void hear(final TypeLiteral<T> type, final TypeEncounter<T> encounter) {
		Class<? super T> clazz = type.getRawType();
		while (clazz != Object.class) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(InjectConfig.class)) {
					encounter.register(new ConfigurationMembersInjector<T>(field, configurationProvider));
				}
			}
			clazz = clazz.getSuperclass();
		}
	}
}
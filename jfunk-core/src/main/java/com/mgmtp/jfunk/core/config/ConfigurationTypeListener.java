/*
 * Copyright (c) 2014 mgm technology partners GmbH
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
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
package com.mgmtp.jfunk.data.generator.config;

import java.io.IOException;

import javax.inject.Singleton;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.data.FieldGenerator;
import com.mgmtp.jfunk.data.generator.data.FormDataFactory;
import com.mgmtp.jfunk.data.generator.data.GeneratorDataSource;
import com.mgmtp.jfunk.data.generator.util.LoremIpsumGenerator;
import com.mgmtp.jfunk.data.source.DataSource;

/**
 * Guice module for the classic jFunk generator.
 * 
 */
public class GeneratorModule extends AbstractModule {

	@Override
	protected void configure() {
		// Create container for FieldGenerators. jFunk itself does not provide implementations.
		MapBinder.newMapBinder(binder(), String.class, FieldGenerator.class);

		// Factory interface for FormData
		install(new FactoryModuleBuilder().build(FormDataFactory.class));

		// Bind the GeneratorDataSource
		MapBinder<String, DataSource> dataSources = MapBinder.newMapBinder(binder(), String.class, DataSource.class);
		dataSources.addBinding("generator").to(GeneratorDataSource.class);

		bind(LoremIpsumGenerator.class);
	}

	@Provides
	Generator provideGenerator(final MathRandom mathRandom, final Configuration config, final Injector injector) {
		boolean ignoreOptionalConstraints = config.getBoolean(JFunkConstants.IGNORE_CONSTRAINT_OPTIONAL);
		return new Generator(mathRandom, ignoreOptionalConstraints, config, injector);
	}

	@Provides
	@Singleton
	@LoremIpsum
	String provideLoremIpsum() throws IOException {
		return Resources.toString(Resources.getResource("lorem_ipsum.txt"), Charsets.UTF_8);
	}
}
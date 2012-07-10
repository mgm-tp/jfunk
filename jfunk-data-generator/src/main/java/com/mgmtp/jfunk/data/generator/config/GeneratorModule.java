package com.mgmtp.jfunk.data.generator.config;

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
import com.mgmtp.jfunk.data.source.DataSource;

/**
 * Guice module for the classic jFunk generator.
 * 
 * @version $Id$
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
	}

	@Provides
	Generator provideGenerator(final MathRandom mathRandom, final Configuration config, final Injector injector) {
		boolean ignoreOptionalConstraints = config.getBoolean(JFunkConstants.IGNORE_CONSTRAINT_OPTIONAL);
		return new Generator(mathRandom, ignoreOptionalConstraints, config, injector);
	}
}
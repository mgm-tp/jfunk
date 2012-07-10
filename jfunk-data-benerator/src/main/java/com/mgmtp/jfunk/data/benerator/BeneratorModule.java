package com.mgmtp.jfunk.data.benerator;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.mgmtp.jfunk.data.source.DataSource;

/**
 * Guice module for the classic jFunk generator.
 * 
 * @version $Id$
 */
public class BeneratorModule extends AbstractModule {

	@Override
	protected void configure() {
		// Bind the BeneratorDataSource
		MapBinder<String, DataSource> dataSources = MapBinder.newMapBinder(binder(), String.class, DataSource.class);
		dataSources.addBinding("benerator").to(BeneratorDataSource.class);
	}
}
/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data;

import com.google.inject.AbstractModule;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.mgmtp.jfunk.data.source.DataSource;

/**
 * Base class for guice modules for data source bindings.
 * 
 * @author rnaegele
 * @version $Id$
 */
public abstract class BaseDataSourceModule extends AbstractModule {

	protected MapBinder<String, DataSource> dataSourcesBinder;

	@Override
	protected void configure() {
		dataSourcesBinder = MapBinder.newMapBinder(binder(), String.class, DataSource.class);
		doConfigure();
	}

	/**
	 * @see #configure()
	 */
	protected abstract void doConfigure();

	/**
	 * Binds a {@link DataSource} under the specified key.
	 * 
	 * @see Multibinder#addBinding()
	 * @return a binding build used to add a {@link DataSource}
	 */
	protected LinkedBindingBuilder<DataSource> bindDataSource(final String key) {
		return dataSourcesBinder.addBinding(key);
	}
}

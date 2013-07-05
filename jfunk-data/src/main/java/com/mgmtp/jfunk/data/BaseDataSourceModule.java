/*
 * Copyright (c) 2013 mgm technology partners GmbH
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

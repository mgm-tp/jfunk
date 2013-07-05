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
package com.mgmtp.jfunk.core.config;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.mgmtp.jfunk.core.JFunk;
import com.mgmtp.jfunk.core.JFunkBase;
import com.mgmtp.jfunk.core.JFunkFactory;

/**
 * Guice module for jFunk in non-server mode.
 * 
 */
public class JFunkDefaultModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new JFunkBaseModule());

		Module factoryModule = new FactoryModuleBuilder().implement(JFunkBase.class, JFunk.class).build(JFunkFactory.class);
		binder().install(factoryModule);
	}
}
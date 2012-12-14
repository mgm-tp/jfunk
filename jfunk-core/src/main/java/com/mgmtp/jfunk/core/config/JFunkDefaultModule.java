/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
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
 * @version $Id$
 */
public class JFunkDefaultModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new JFunkBaseModule());

		Module factoryModule = new FactoryModuleBuilder().implement(JFunkBase.class, JFunk.class).build(JFunkFactory.class);
		binder().install(factoryModule);
	}
}
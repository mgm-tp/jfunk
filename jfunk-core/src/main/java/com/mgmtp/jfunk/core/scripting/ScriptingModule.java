/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.scripting;

import com.mgmtp.jfunk.core.config.BaseJFunkGuiceModule;

/**
 * @author rnaegele
 */
public class ScriptingModule extends BaseJFunkGuiceModule {

	@Override
	protected void doConfigure() {
		bind(StepExecutor.class);
		bind(ScriptContext.class);
		bind(ModuleArchiver.class);
		bind(ScriptExecutor.class);
		bind(ModuleBuilder.class);

		bindEventHandler().to(InternalEventHandler.class);
	}
}

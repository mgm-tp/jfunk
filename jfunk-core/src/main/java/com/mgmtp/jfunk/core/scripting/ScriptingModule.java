package com.mgmtp.jfunk.core.scripting;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.mgmtp.jfunk.core.config.BaseJFunkGuiceModule;

/**
 * @author rnaegele
 * @version $Id$
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

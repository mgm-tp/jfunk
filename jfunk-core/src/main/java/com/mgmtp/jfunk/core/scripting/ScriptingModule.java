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

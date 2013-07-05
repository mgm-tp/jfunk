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

import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.io.IOUtils;

import com.google.common.base.Charsets;
import com.google.common.eventbus.EventBus;
import com.google.inject.Provides;
import com.google.inject.Scope;
import com.google.inject.matcher.Matchers;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.config.ModuleScoped;
import com.mgmtp.jfunk.common.config.ScriptScoped;
import com.mgmtp.jfunk.common.config.StackedScope;
import com.mgmtp.jfunk.common.config.ThreadScope;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.data.DataSetAdapter;
import com.mgmtp.jfunk.core.event.AfterCommandEvent;
import com.mgmtp.jfunk.core.event.BeforeCommandEvent;
import com.mgmtp.jfunk.core.event.EventHandlers;
import com.mgmtp.jfunk.core.reporting.ReportData;
import com.mgmtp.jfunk.core.scripting.BreakIndex;
import com.mgmtp.jfunk.core.scripting.Cmd;
import com.mgmtp.jfunk.core.scripting.ExecutionMode;
import com.mgmtp.jfunk.core.scripting.ModuleArchiver;
import com.mgmtp.jfunk.core.scripting.ScriptContext;
import com.mgmtp.jfunk.core.scripting.ScriptingModule;
import com.mgmtp.jfunk.core.util.CsvDataProcessor;
import com.mgmtp.jfunk.data.DataSourceModule;

/**
 * Guice module for jFunk which is always needed. It is loaded automatically by the
 * {@link ModulesLoader}. Additional modules need to be configured in a properties file (see
 * {@link ModulesLoader}.
 * 
 */
public final class JFunkBaseModule extends BaseJFunkGuiceModule {

	@Override
	protected void doConfigure() {
		ThreadScope scope = new ThreadScope();
		scope.enterScope(); // need to enter it right away for the main thread

		bindScope(ThreadScope.class, scope, ScriptScoped.class);
		bindScope(StackedScope.class, new StackedScope(), ModuleScoped.class);

		bindListener(Matchers.any(), new ConfigurationTypeListener(getProvider(Configuration.class)));

		bind(DataSetAdapter.class);
		bind(CsvDataProcessor.class);

		bindCommandInterceptor();

		String encoding = System.getProperty("file.encoding");
		Charset charset = null;
		try {
			charset = Charset.forName(encoding);
		} catch (IllegalArgumentException ex) {
			charset = Charsets.UTF_8;
		}
		bind(Charset.class).toInstance(charset);

		install(new DataSourceModule());
		install(new ScriptingModule());
	}

	private <T extends Scope> void bindScope(final Class<T> scopeClass, final T scope,
			final Class<? extends Annotation> scopeAnnotation) {
		// We need a custom thread scope for things related to test runs, because each test runs in
		// its own thread. This gives us thread-local Guice singletons.
		bindScope(scopeAnnotation, scope);

		// We also need to get a hold of the ThreadScope instance via Guice in order to be able to
		// call its cleanUp method after a thread is done. We need to do a clean-up in order to
		// avoid memory leaks.
		bind(scopeClass).toInstance(scope);
	}

	private void bindCommandInterceptor() {
		MethodInterceptor interceptor = new MethodInterceptor() {
			@Inject
			Provider<EventBus> eventBusProvider;

			@Override
			public Object invoke(final MethodInvocation invocation) throws Throwable { //NOSONAR
				String command = invocation.getMethod().getName();
				Object[] params = invocation.getArguments();

				boolean success = false;
				EventBus eventBus = eventBusProvider.get();
				try {
					eventBus.post(new BeforeCommandEvent(command, params));
					Object result = invocation.proceed();
					success = true;
					return result;
				} finally {
					eventBus.post(new AfterCommandEvent(command, params, success));
				}
			}
		};
		requestInjection(interceptor);
		bindInterceptor(Matchers.subclassesOf(ScriptContext.class), Matchers.annotatedWith(Cmd.class), interceptor);
	}

	/**
	 * Provides the version of perfLoad as specified in the Maven pom.
	 * 
	 * @return the version string
	 */
	@Provides
	@Singleton
	@JFunkVersion
	protected String providePerfLoadVersion() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream is = loader.getResourceAsStream("com/mgmtp/jfunk/common/version.txt");
		try {
			return IOUtils.toString(is, "UTF-8");
		} catch (IOException ex) {
			throw new IllegalStateException("Could not read jFunk version.", ex);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	@Provides
	@ScriptScoped
	Deque<ReportData> provideReportDataStack() {
		return new ArrayDeque<ReportData>();
	}

	@Provides
	@ScriptScoped
	Configuration provideConfiguration(final DataSetAdapter dsAdapter, final Charset charset) {
		return new Configuration(dsAdapter, charset);
	}

	@Provides
	@Singleton
	EventBus provideEventBus(@EventHandlers final Set<Object> eventHandlers) {
		EventBus eventBus = new EventBus();
		for (Object eventHandler : eventHandlers) {
			eventBus.register(eventHandler);
		}
		return eventBus;
	}

	@Provides
	@ArchiveDir
	File provideArchiveDir(final Configuration config) {
		File archiveDir = new File(config.get(JFunkConstants.ARCHIVE_DIR, JFunkConstants.ARCHIVE_DIR_DEFAULT)).getAbsoluteFile();
		archiveDir.mkdirs();
		checkState(archiveDir.exists(), "Could not create archive directory: %s", archiveDir);
		return archiveDir;
	}

	@Provides
	@ModuleArchiveDir
	File provideModuleArchiveDir(final Provider<ModuleArchiver> moduleArchiverProvider) {
		return moduleArchiverProvider.get().getModuleArchiveDir();
	}

	@Provides
	@ScriptScoped
	MathRandom provideMathRandom(final Configuration config) {
		String seedString = config.get(JFunkConstants.RANDOM_SEED, false);

		MathRandom mathRandom;
		if (seedString == null) {
			mathRandom = new MathRandom();
			if (config.get(JFunkConstants.RANDOM_SEED, false) == null) {
				config.put(JFunkConstants.RANDOM_SEED, String.valueOf(mathRandom.getSeed()));
			}
		} else {
			mathRandom = new MathRandom(Long.parseLong(seedString));
		}
		return mathRandom;
	}

	@Provides
	ExecutionMode provideExecutionMode(final Configuration config) {
		String execMode = config.get(JFunkConstants.EXECUTION_MODE, JFunkConstants.EXECUTION_MODE_ALL);
		return ExecutionMode.valueOf(execMode);
	}

	@Provides
	@BreakIndex
	int provideBreakIndex(final Configuration config) {
		return config.getInteger(JFunkConstants.STEP, 0);
	}
}

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
package com.mgmtp.jfunk.core.scripting;

import java.io.IOException;
import java.util.Date;
import java.util.Deque;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.log4j.Logger;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mgmtp.jfunk.core.config.ModuleStartDate;
import com.mgmtp.jfunk.core.event.AfterRunEvent;
import com.mgmtp.jfunk.core.event.AfterScriptEvent;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.core.reporting.ReportContext;
import com.mgmtp.jfunk.core.reporting.Reported;
import com.mgmtp.jfunk.core.reporting.Reporter;
import com.mgmtp.jfunk.core.step.base.Step;

/**
 * @author rnaegele
 */
@Singleton
class InternalEventHandler {
	private final Logger log = Logger.getLogger(getClass());

	private final Provider<ModuleArchiver> moduleArchiverProvider;
	private final Provider<ScriptContext> scriptContextProvider;
	private final Provider<Deque<ReportContext>> reportContextStackProvider;
	private final Set<Reporter> globalReporters;
	private final Provider<Date> moduleStartDateProvider;

	@Inject
	InternalEventHandler(final Provider<ModuleArchiver> moduleArchiverProvider,
			final Provider<ScriptContext> scriptContextProvider, final Provider<Deque<ReportContext>> reportContextStackProvider,
			final Set<Reporter> globalReporters, @ModuleStartDate final Provider<Date> moduleStartDateProvider) {
		this.moduleArchiverProvider = moduleArchiverProvider;
		this.scriptContextProvider = scriptContextProvider;
		this.reportContextStackProvider = reportContextStackProvider;
		this.globalReporters = globalReporters;
		this.moduleStartDateProvider = moduleStartDateProvider;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void handleBeforeModule(final InternalBeforeModuleEvent event) {
		TestModule module = event.getModule();
		moduleArchiverProvider.get().startArchiving(module);

		// if not explititly disabled, module are always reported
		Reported reported = module.getClass().getAnnotation(Reported.class);
		if (reported == null || reported.value()) {
			ReportContext reportContext = new ReportContext();
			reportContext.setTestObjectName(module.getName());
			reportContext.setTestObjectType(module.getClass());
			reportContext.setStartMillis(moduleStartDateProvider.get().getTime());
			reportContextStackProvider.get().push(reportContext);
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void handleAfterModule(final InternalAfterModuleEvent event) {
		TestModule module = event.getModule();

		try {
			// if not explititly disabled, module are always reported
			Reported reported = module.getClass().getAnnotation(Reported.class);
			if (reported == null || reported.value()) {
				ReportContext reportContext = reportContextStackProvider.get().pop();
				reportContext.setStopMillis(System.currentTimeMillis());
				reportContext.setThrowable(event.getThrowable());
				addReportResults(reportContext);
			}
		} finally {
			moduleArchiverProvider.get().finishArchiving(module, event.getThrowable());
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void handleBeforeStep(final InternalBeforeStepEvent event) {
		Step step = event.getStep();
		Class<? extends Step> stepClass = step.getClass();
		Reported reported = stepClass.getAnnotation(Reported.class);
		if (reported != null && reported.value()) {
			ReportContext reportContext = new ReportContext();
			reportContext.setTestObjectName(step.getName());
			reportContext.setTestObjectType(stepClass);
			reportContext.setStartMillis(System.currentTimeMillis());
			reportContextStackProvider.get().push(reportContext);
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void handleAfterStep(final InternalAfterStepEvent event) {
		Step step = event.getStep();
		Reported reported = step.getClass().getAnnotation(Reported.class);
		if (reported != null && reported.value()) {
			ReportContext reportContext = reportContextStackProvider.get().pop();
			reportContext.setStopMillis(System.currentTimeMillis());
			reportContext.setThrowable(event.getThrowable());
			addReportResults(reportContext);
		}
	}

	private void addReportResults(final ReportContext reportContext) {
		Set<Reporter> scriptReporters = scriptContextProvider.get().getReporters();
		for (Reporter reporter : scriptReporters) {
			reporter.addResult(reportContext);
		}

		for (Reporter reporter : globalReporters) {
			reporter.addResult(reportContext);
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void handleAfterScript(@SuppressWarnings("unused") final AfterScriptEvent event) {
		Set<Reporter> scriptReporters = scriptContextProvider.get().getReporters();
		for (Reporter reporter : scriptReporters) {
			createReport(reporter);
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void handleAfterRun(@SuppressWarnings("unused") final AfterRunEvent event) {
		for (Reporter report : globalReporters) {
			createReport(report);
		}
	}

	private void createReport(final Reporter reporter) {
		try {
			reporter.createReport();
		} catch (IOException ex) {
			log.error("Error creating report", ex);
		}
	}
}

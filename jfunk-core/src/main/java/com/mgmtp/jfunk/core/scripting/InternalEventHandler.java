/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.scripting;

import java.io.IOException;
import java.util.Deque;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.log4j.Logger;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mgmtp.jfunk.common.util.Disposable;
import com.mgmtp.jfunk.core.event.AfterRunEvent;
import com.mgmtp.jfunk.core.event.AfterScriptEvent;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.core.reporting.ReportData;
import com.mgmtp.jfunk.core.reporting.Reported;
import com.mgmtp.jfunk.core.reporting.Reporter;
import com.mgmtp.jfunk.core.step.base.Step;

/**
 * @author rnaegele
 * @version $Id$
 */
@Singleton
class InternalEventHandler {
	private final Logger log = Logger.getLogger(getClass());

	private final Provider<ModuleArchiver> moduleArchiverProvider;
	private final Provider<ScriptContext> scriptContextProvider;
	private final Provider<Deque<ReportData>> reportDataStackProvider;
	private final Set<Reporter> globalReporters;

	@Inject
	InternalEventHandler(final Provider<ModuleArchiver> moduleArchiverProvider,
			final Provider<ScriptContext> scriptContextProvider,
			final Provider<Deque<ReportData>> reportDataStackProvider, final Set<Reporter> globalReporters) {
		this.moduleArchiverProvider = moduleArchiverProvider;
		this.scriptContextProvider = scriptContextProvider;
		this.reportDataStackProvider = reportDataStackProvider;
		this.globalReporters = globalReporters;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void handleBeforeModule(final InternalBeforeModuleEvent event) {
		TestModule module = event.getModule();
		moduleArchiverProvider.get().startArchiving(module);

		// if not explititly disablewd, module are always reported
		Reported reported = module.getClass().getAnnotation(Reported.class);
		if (reported == null || reported.value()) {
			ReportData reportData = new ReportData(module);
			reportData.setStartMillis(System.currentTimeMillis());
			reportDataStackProvider.get().push(reportData);
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void handleAfterModule(final InternalAfterModuleEvent event) {
		TestModule module = event.getModule();

		try {
			// if not explititly disablewd, module are always reported
			Reported reported = module.getClass().getAnnotation(Reported.class);
			if (reported == null || reported.value()) {
				ReportData reportData = reportDataStackProvider.get().pop();
				reportData.setStopMillis(System.currentTimeMillis());
				reportData.setThrowable(event.getThrowable());
				addReportResults(reportData);
			}
		} finally {
			moduleArchiverProvider.get().finishArchiving(module, event.getThrowable());
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void handleBeforeStep(final InternalBeforeStepEvent event) {
		Step step = event.getStep();
		Reported reported = step.getClass().getAnnotation(Reported.class);
		if (reported != null && reported.value()) {
			ReportData reportData = new ReportData(event.getStep());
			reportData.setStartMillis(System.currentTimeMillis());
			reportDataStackProvider.get().push(reportData);
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void handleAfterStep(final InternalAfterStepEvent event) {
		Step step = event.getStep();
		Reported reported = step.getClass().getAnnotation(Reported.class);
		if (reported != null && reported.value()) {
			ReportData reportData = reportDataStackProvider.get().pop();
			reportData.setStopMillis(System.currentTimeMillis());
			reportData.setThrowable(event.getThrowable());
			addReportResults(reportData);
		}
	}

	private void addReportResults(final ReportData reportData) {
		Set<Reporter> scriptReporters = scriptContextProvider.get().getReporters();
		for (Reporter reporter : scriptReporters) {
			reporter.addResult(reportData);
		}

		for (Reporter reporter : globalReporters) {
			reporter.addResult(reportData);
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

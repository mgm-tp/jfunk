/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.step.base;

import com.mgmtp.jfunk.core.module.TestModule;

/**
 * Base class for repeated execution of a list of steps.
 * 
 * @see ComplexStep
 * @version $Id$
 */
public abstract class BaseRepeatComplexStep extends ComplexStep {

	public BaseRepeatComplexStep(final String name, final String dataSetKey, final StepMode stepMode) {
		super(name, dataSetKey, stepMode);
	}

	public BaseRepeatComplexStep(final String name, final String dataSetKey) {
		super(name, dataSetKey);
	}

	/**
	 * @param name
	 *            the name of the step (if {@code null}, {@code getClass().getSimpleName()} is used)
	 * @param stepMode
	 *            the step mode (if {@code null}, {@link StepMode#SET_VALUE} is used)
	 */
	public BaseRepeatComplexStep(final String name, final StepMode stepMode) {
		super(name, stepMode);
	}

	/**
	 * @param dataSetKey
	 *            the data set key
	 */
	public BaseRepeatComplexStep(final String dataSetKey) {
		super(dataSetKey);
	}

	/**
	 * @param stepMode
	 *            the step mode (if {@code null}, {@link StepMode#SET_VALUE} is used)
	 */
	public BaseRepeatComplexStep(final StepMode stepMode) {
		super(stepMode);
	}

	public BaseRepeatComplexStep() {
		super();
	}

	/**
	 * @param testModule
	 *            param no longer used
	 */
	@Deprecated
	protected BaseRepeatComplexStep(final TestModule testModule) {
		super(testModule);
	}

	/**
	 * @param testModule
	 *            param no longer used
	 */
	@Deprecated
	public BaseRepeatComplexStep(final TestModule testModule, final StepMode stepMode) {
		super(testModule, stepMode);
	}

	@Deprecated
	public BaseRepeatComplexStep(final TestModule testModule, final String name, final String dataSetKey, final StepMode stepMode) {
		super(testModule, name, dataSetKey, stepMode);
	}

	/**
	 * The index of the current run.
	 */
	protected int currentIndex;

	/**
	 * Defines how many runs are to be executed.
	 * 
	 * @return The number of times the configured steps (see {@link #executeSteps()}) are executed.
	 */
	protected abstract int getNumberOfRuns();

	/**
	 * Defines what the first index of this run is to be.
	 * 
	 * @return the index to start at
	 */
	protected int getStartIndex() {
		return 1;
	}

	/**
	 * Calls {@code super.execute()} in a loop the number of times specified by
	 * {@link #getNumberOfRuns()}. Before the loop, {@link #prepareExecute()} is called, after the
	 * loop, {@link #finishExecute()}. Before and after each loop iteration, {@link #prepareRun()}
	 * and {@link #finishExecute()} are called, respectively.
	 */
	@Override
	public void execute() {
		if (StepMode.NONE == stepMode) {
			return;
		}

		executing = true;
		prepareExecute();
		int anzahl = getNumberOfRuns();
		log.info("Executing " + anzahl + " runs");
		for (currentIndex = getStartIndex(); currentIndex <= anzahl; currentIndex++) {
			prepareRun();
			if (log.isDebugEnabled()) {
				log.debug("... run #" + currentIndex);
			}
			super.execute();
			executing = true;
			finishRun();
		}
		finishExecute();
		executing = false;
		if (log.isDebugEnabled()) {
			log.debug("Runs finished");
		}
	}

	/**
	 * Called once before the runs are executed. The default implementation does nothing.
	 */
	protected void prepareExecute() {
		// default implementation is empty
	}

	/**
	 * Called after all runs have been executed. The default implementation does nothing.
	 */
	protected void finishExecute() {
		// default implementation is empty
	}

	/**
	 * Called before every single run (i. e. loop iteration). The default implementation does
	 * nothing.
	 */
	protected void prepareRun() {
		// default implementation is empty
	}

	/**
	 * Called after every single run (i. e. loop iteration). The default implementation does
	 * nothing.
	 */
	protected void finishRun() {
		// default implementation is empty
	}
}
/*
 * Copyright (c) 2015 mgm technology partners GmbH
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
package com.mgmtp.jfunk.core.step.base;


/**
 * Base class for repeated execution of a list of steps.
 * 
 * @see ComplexStep
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
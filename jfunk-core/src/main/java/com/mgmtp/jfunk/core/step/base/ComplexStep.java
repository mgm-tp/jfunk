/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.step.base;

import static com.google.common.base.Preconditions.checkState;

import javax.inject.Inject;

import com.mgmtp.jfunk.core.scripting.StepExecutor;

/**
 * Base class that allows for grouping a number of related steps.
 * 
 */
public abstract class ComplexStep extends DataSetsStep {

	/**
	 * The associated {@link StepMode} instance. Steps added to the internal list may evaluate the
	 * step mode.
	 */
	protected final StepMode stepMode;

	private StepExecutor stepExecutor;

	protected boolean executing;
	private int execCounter;

	/**
	 * @param dataSetKey
	 *            the data set key
	 * @param stepMode
	 *            the step mode (if {@code null}, {@link StepMode#SET_VALUE} is used)
	 */
	public ComplexStep(final String dataSetKey, final StepMode stepMode) {
		this(null, dataSetKey, stepMode);
	}

	/**
	 * @param dataSetKey
	 *            the data set key
	 * @param stepMode
	 *            the step mode (if {@code null}, {@link StepMode#SET_VALUE} is used)
	 */
	public ComplexStep(final String name, final String dataSetKey, final StepMode stepMode) {
		super(name, dataSetKey);
		this.stepMode = stepMode == null ? StepMode.SET_VALUE : stepMode;
	}

	/**
	 * @param name
	 *            the name of the step (if {@code null}, {@code getClass().getSimpleName()} is used)
	 */
	public ComplexStep(final String name, final String dataSetKey) {
		this(name, dataSetKey, null);
	}

	/**
	 * @param dataSetKey
	 *            the data set key
	 */
	public ComplexStep(final String dataSetKey) {
		this(null, dataSetKey);
	}

	/**
	 * @param stepMode
	 *            the step mode (if {@code null}, {@link StepMode#SET_VALUE} is used)
	 */
	public ComplexStep(final StepMode stepMode) {
		this((String) null, stepMode);
	}

	public ComplexStep() {
		this(null, null, null);
	}

	/**
	 * Executes the given step. This method must be called from within {@link #executeSteps()}.
	 * 
	 * @param step
	 *            the step to execute
	 */
	protected void executeStep(final Step step) {
		checkState(executing, "executeStep() must be called from within executeSteps()!");
		stepExecutor.executeStep(step, execCounter++);
	}

	/**
	 * Executes the given steps. This method must be called from within {@link #executeSteps()}.
	 * 
	 * @param steps
	 *            the steps to execute
	 */
	protected void executeSteps(final Step... steps) {
		for (Step step : steps) {
			executeStep(step);
		}
	}

	/**
	 * Override this method in order to specify the child steps to be executed when this step is run
	 * calling {@link #executeStep(Step)}, or {@link #executeSteps(Step...)}
	 */
	protected void executeSteps() {
		// no-op
	}

	/**
	 * Calls {@link #executeSteps()}. This method should normally not be overridden.
	 */
	@Override
	public void execute() {
		executing = true;
		try {
			executeSteps();
		} finally {
			executing = false;
		}
	}

	/**
	 * @return the stepMode
	 */
	public StepMode getStepMode() {
		return stepMode;
	}

	/**
	 * @return the stepExecutor
	 */
	protected StepExecutor getStepExecutor() {
		return stepExecutor;
	}

	/**
	 * @param stepExecutor
	 *            the stepExecutor to set
	 */
	@Inject
	protected void setStepExecutor(final StepExecutor stepExecutor) {
		this.stepExecutor = stepExecutor;
	}
}
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
package com.mgmtp.jfunk.core.module;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayListWithCapacity;

import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.core.scripting.BreakIndex;
import com.mgmtp.jfunk.core.scripting.ExecutionMode;
import com.mgmtp.jfunk.core.scripting.StepExecutor;
import com.mgmtp.jfunk.core.step.base.Step;
import com.mgmtp.jfunk.data.DataSet;

/**
 * <p>
 * Default {@link TestModule} implementation. Implementers must override {@link #executeSteps()}
 * calling {@link #executeStep(Step)}, {@link #executeStep(Step, boolean)}, or
 * {@link #executeSteps(Step...)} in order to add steps to be executed. Prior to jFunk 3.0 step
 * execution was deferred. Steps were first collected in an internal list and then executed
 * sequentially. This behavior has been changed. Steps are now executed immediately when calling one
 * of these methods.
 * </p>
 * <p>
 * In order to enable migration tests, break steps may be added using
 * {@link #executeStep(Step, boolean) executeStep(Step, true)}. This allows a test module to be run
 * up to and including certain steps and to finish off e. g. against the next release of the system
 * under test. Multiple break steps may be added. They are registered and indexed internally in the
 * order of their addition. The actual behavior depends on the {@link ExecutionMode} which can be
 * set with the property {@link JFunkConstants#EXECUTION_MODE}. For execution modes other than
 * {@link ExecutionMode#all}, the index of the break step must be set with the property
 * {@link JFunkConstants#STEP}.
 * </p>
 * <p>
 * <b>Execution modes in detail:</b>
 * <dl>
 * <dt>{@link ExecutionMode#start}</dt>
 * <dd>Execute steps from start up to and including the break step with the given break index and
 * skip remaining steps.</dd>
 * <dt>{@link ExecutionMode#finish}</dt>
 * <dd>Skip first steps up to and including that with the given break index and execute remaining
 * steps.
 * <dt>{@link ExecutionMode#all}</dd>
 * <dd>Execute all steps. A potentially specified break index is ignored.</dd>
 * <dl>
 * </p>
 * 
 * @see ExecutionMode
 * @author rnaegele
 */
@NotThreadSafe
public class TestModuleImpl implements TestModule {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	StepExecutor stepExecutor;

	@Inject
	Map<String, DataSet> dataSets;

	@Inject
	ExecutionMode executionMode;

	@Inject
	@BreakIndex
	int breakIndex;

	private final String name;
	private String dataSetKey;

	private final List<Step> breaksList = newArrayListWithCapacity(1);

	private int execCounter;
	private boolean skip;

	protected boolean executing;

	private boolean error;

	/**
	 * Creates a new instance with the specified data set key. This key is used internally for
	 * retrieving the default {@link DataSet} for this test module in {@link #getDataSet()}.
	 * 
	 * @param dataSetKey
	 *            the data set key
	 */
	public TestModuleImpl(final String dataSetKey) {
		this(null, dataSetKey);
	}

	/**
	 * Creates a new instance with the specified name and data set key. This key is used internally
	 * for retrieving the default {@link DataSet} for this test module in {@link #getDataSet()}.
	 * 
	 * @param name
	 *            the module's name
	 * @param dataSetKey
	 *            the data set key
	 */
	public TestModuleImpl(final String name, final String dataSetKey) {
		this.name = name == null ? getClass().getSimpleName() : name;
		this.dataSetKey = dataSetKey;
	}

	/**
	 * Override this method in order to specify the steps to be executed when this test module is
	 * run calling {@link #executeStep(Step)}, {@link #executeStep(Step, boolean)}, or
	 * {@link #executeSteps(Step...)}
	 */
	protected void executeSteps() {
		// no-op
	}

	/**
	 * Returns the test module's default {@link DataSet}.
	 * 
	 * @return the test module's default {@link DataSet}
	 */
	protected DataSet getDataSet() {
		return dataSets != null && dataSetKey != null ? dataSets.get(dataSetKey) : null;
	}

	/**
	 * Returns the {@link DataSet} for the specified key.
	 * 
	 * @param key
	 *            the data set key
	 * @return the {@link DataSet}
	 */
	protected DataSet getDataSet(final String key) {
		return dataSets.get(key);
	}

	/**
	 * Returns this test module's name.
	 * 
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDataSetKey() {
		return dataSetKey;
	}

	/**
	 * @param dataSetKey
	 *            the dataSetKey to set
	 */
	@Override
	public void setDataSetKey(final String dataSetKey) {
		this.dataSetKey = dataSetKey;
	}

	/**
	 * Returns this test module's execution mode.
	 * 
	 * @return the executionMode
	 */
	public ExecutionMode getExecutionMode() {
		return executionMode;
	}

	/**
	 * Returns this test module's break index.
	 * 
	 * @return the breakIndex
	 */
	public int getBreakIndex() {
		return breakIndex;
	}

	/**
	 * Executes the specified step. This method must be called from within {@link #executeSteps()}.
	 * 
	 * @param step
	 *            the step to execute
	 */
	protected void executeStep(final Step step) {
		executeStep(step, false);
	}

	/**
	 * Executes the specified steps. This method must be called from within {@link #executeSteps()}.
	 * 
	 * @param steps
	 *            the steps to execute
	 */
	protected void executeSteps(final Step... steps) {
		for (Step step : steps) {
			executeStep(step, false);
		}
	}

	/**
	 * Executes the specified step. This method must be called from within {@link #executeSteps()}.
	 * 
	 * @param step
	 *            the step to execute
	 * @param isBreakStep
	 *            if {@code true}, the specified step is registered as a break step
	 */
	protected void executeStep(final Step step, final boolean isBreakStep) {
		checkState(executing, "executeStep() must be called from withing executeSteps()!");

		if (executionMode == ExecutionMode.all) {

			// execute complete module
			stepExecutor.executeStep(step, execCounter);

		} else {

			if (isBreakStep) {
				breaksList.add(step);
			}
			if (!skip) {
				stepExecutor.executeStep(step, execCounter);
			}

			// determine break step from list of break steps
			Step breakStep = breakIndex < breaksList.size() ? breaksList.get(breakIndex) : null;

			if (step.equals(breakStep)) {
				// toggle skip flag
				switch (executionMode) {
					case start:
						skip = true;
						break;
					case finish:
						skip = false;
						break;
					default:
						throw new IllegalStateException("Invalid execution mode: " + executionMode);
				}
			}
		}
		execCounter++;
	}

	/**
	 * Calls {@link #executeSteps()}, thus executing the test module with respect to the
	 * {@link ExecutionMode}.
	 */
	@Override
	public void execute() {
		executing = true;
		try {
			log.info("Running module: " + this);
			// - ExecutionMode.start:
			//   Execute steps from start up to and including the break step with the given break index
			//   and skip remaining steps
			// - ExecutionMode.finish:
			//   Skip first steps up to and including that with the given break index
			//   and execute remaining steps
			// - ExecutionMode.all: Execute all steps. Skip flag is ignored
			skip = executionMode == ExecutionMode.finish;
			executeSteps();
		} finally {
			executing = false;
		}
	}

	/**
	 * @return the error
	 */
	@Override
	public boolean isError() {
		return error;
	}

	/**
	 * Setting the error flag allows a module to be explicitly set to an error state.
	 * 
	 * @param error
	 *            the error to set
	 */
	@Override
	public void setError(final boolean error) {
		this.error = error;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		tsb.append("name", getName());
		tsb.append("executionMode", executionMode);
		tsb.append("breakIndex", breakIndex);
		return tsb.toString();
	}
}
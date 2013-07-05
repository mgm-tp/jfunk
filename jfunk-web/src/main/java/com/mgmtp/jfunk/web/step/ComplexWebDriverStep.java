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
package com.mgmtp.jfunk.web.step;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;

import com.mgmtp.jfunk.core.step.base.ComplexStep;
import com.mgmtp.jfunk.core.step.base.StepMode;
import com.mgmtp.jfunk.web.util.FormInputHandler;
import com.mgmtp.jfunk.web.util.WebDriverTool;
import com.mgmtp.jfunk.web.util.WebElementFinder;

/**
 * {@link ComplexStep} descendant that gets a number of {@link WebDriver}-related utility classe
 * injected.
 * 
 * @author rnaegele
 */
public class ComplexWebDriverStep extends ComplexStep {

	@Inject
	protected WebDriverTool wdt;

	@Inject
	protected WebDriver webDriver;

	@Inject
	protected WebElementFinder wef;

	@Inject
	protected FormInputHandler fih;

	/**
	 * @param dataSetKey
	 *            the data set key
	 * @param stepMode
	 *            the step mode (if {@code null}, {@link StepMode#SET_VALUE} is used)
	 */
	public ComplexWebDriverStep(final String dataSetKey, final StepMode stepMode) {
		super(dataSetKey, stepMode);
	}

	/**
	 * @param dataSetKey
	 *            the data set key
	 * @param stepMode
	 *            the step mode (if {@code null}, {@link StepMode#SET_VALUE} is used)
	 */
	public ComplexWebDriverStep(final String name, final String dataSetKey, final StepMode stepMode) {
		super(name, dataSetKey, stepMode);
	}

	/**
	 * @param name
	 *            the name of the step (if {@code null}, {@code getClass().getSimpleName()} is used)
	 */
	public ComplexWebDriverStep(final String name, final String dataSetKey) {
		super(name, dataSetKey);
	}

	/**
	 * @param dataSetKey
	 *            the data set key
	 */
	public ComplexWebDriverStep(final String dataSetKey) {
		super(dataSetKey);
	}

	/**
	 * @param stepMode
	 *            the step mode (if {@code null}, {@link StepMode#SET_VALUE} is used)
	 */
	public ComplexWebDriverStep(final StepMode stepMode) {
		super(stepMode);
	}

	public ComplexWebDriverStep() {
		super();
	}
}

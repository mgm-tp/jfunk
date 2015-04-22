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
package com.mgmtp.jfunk.core.reporting;

import java.io.IOException;

import com.mgmtp.jfunk.core.step.base.Step;

/**
 * Interface for reporters. A reporter reports module results. Reporting on {@link Step}-level is
 * also possible for steps annotation with {@link Reported} and if the reporter implementation
 * supports step-evel reporting.
 * 
 */
public interface Reporter {

	/**
	 * Gets the reporter's name.
	 * 
	 * @return the reporter's name
	 */
	String getName();

	/**
	 * Adds a result to the report.
	 * 
	 * @param reportContext
	 *            the report data to add
	 */
	void addResult(ReportContext reportContext);

	/**
	 * Creates the report, e. g. writes the report to a file or sends it as e-mail.
	 */
	void createReport() throws IOException;

}
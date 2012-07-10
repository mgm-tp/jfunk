package com.mgmtp.jfunk.core.reporting;

import java.io.IOException;

import com.mgmtp.jfunk.core.step.base.Step;

/**
 * Interface for reporters. A reporter reports module results. Reporting on {@link Step}-level is
 * also possible for steps annotation with {@link Reported} and if the reporter implementation
 * supports step-evel reporting.
 * 
 * @version $Id$
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
	 * @param reportData
	 *            the report data to add
	 */
	void addResult(ReportData reportData);

	/**
	 * Creates the report, e. g. writes the report to a file or sends it as e-mail.
	 */
	void createReport() throws IOException;

}
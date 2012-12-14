/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.step;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.seleniumemulation.SeleneseCommand;

import com.mgmtp.jfunk.core.module.TestModule;

/**
 * Step for executing {@link SeleneseCommand}s. All {@link SeleneseCommand}s with {@code Void} as
 * type parameter are supported.
 * 
 * @author rnaegele
 * @version $Id$
 */
public class ExecSeleneseCmd extends WebDriverStep {

	private final SeleneseCommand<Void> cmd;
	private final String[] args;

	/**
	 * @param testModule
	 *            param no longer used
	 * @param cmd
	 *            the Selenese command to be executed
	 * @param args
	 *            the arguments for the command
	 */
	@Deprecated
	public ExecSeleneseCmd(final TestModule testModule, final SeleneseCommand<Void> cmd, final String... args) {
		this(cmd, args);
	}

	/**
	 * @param cmd
	 *            the Selenese command to be executed
	 * @param args
	 *            the arguments for the command
	 */
	public ExecSeleneseCmd(final SeleneseCommand<Void> cmd, final String... args) {
		this.cmd = cmd;
		this.args = args;
	}

	/**
	 * Executes the command calling its
	 * {@link SeleneseCommand#apply(org.openqa.selenium.WebDriver, String[]) apply} method with the
	 * current {@link WebDriver} instance and the specified arguments.
	 */
	@Override
	public void execute() {
		cmd.apply(getWebDriver(), args);
	}
}

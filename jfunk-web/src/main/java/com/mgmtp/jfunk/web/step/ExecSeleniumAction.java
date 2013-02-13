/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.step;

import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import com.mgmtp.jfunk.core.exception.StepException;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.core.step.base.BaseStep;

/**
 * Step for executing Selenium actions.
 * 
 * @author rnaegele
 * @version $Id$
 */
public class ExecSeleniumAction extends BaseStep {

	private final Action action;

	/**
	 * @param testModule
	 *            param no longer used
	 * @param actions
	 *            the {@link Actions} instance to call {@link Actions#perform() perform} on
	 */
	@Deprecated
	public ExecSeleniumAction(final TestModule testModule, final Actions actions) {
		this(actions);
	}

	/**
	 * @param testModule
	 *            param no longer used
	 * @param action
	 *            the {@link Action} instance to call {@link Action#perform() perform} on
	 */
	@Deprecated
	public ExecSeleniumAction(final TestModule testModule, final Action action) {
		this(action);
	}

	/**
	 * @param actions
	 *            the {@link Actions} instance to call {@link Actions#perform() perform} on
	 */
	public ExecSeleniumAction(final Actions actions) {
		this(actions.build());
	}

	/**
	 * @param action
	 *            the {@link Action} instance to call {@link Action#perform() perform} on
	 */
	public ExecSeleniumAction(final Action action) {
		this.action = action;
	}

	@Override
	public void execute() throws StepException {
		action.perform();
	}
}

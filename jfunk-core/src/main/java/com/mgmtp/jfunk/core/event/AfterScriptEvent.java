/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.event;


/**
 * @author rnaegele
 */
public class AfterScriptEvent extends ScriptEvent {

	private final boolean success;

	public AfterScriptEvent(final String scriptFileOrTestMethod, final boolean success) {
		super(scriptFileOrTestMethod);
		this.success = success;
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}
}

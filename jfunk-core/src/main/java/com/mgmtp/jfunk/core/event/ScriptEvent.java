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
public abstract class ScriptEvent extends AbstractBaseEvent {

	private final String scriptFileOrTestMethod;

	public ScriptEvent(final String scriptFileOrTestMethod) {
		this.scriptFileOrTestMethod = scriptFileOrTestMethod;
	}

	/**
	 * @return the scriptFileOrTestMethod
	 */
	public String getScriptFileOrTestMethod() {
		return scriptFileOrTestMethod;
	}
}

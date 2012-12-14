/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.scripting;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.mgmtp.jfunk.core.module.TestModule;

/**
 * 
 * @author rnaegele
 * @version $Id$
 */
abstract class InternalModuleEvent {

	private final TestModule module;

	public InternalModuleEvent(final TestModule module) {
		this.module = module;
	}

	/**
	 * @return the module
	 */
	public TestModule getModule() {
		return module;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
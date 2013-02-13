/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.scripting;

import com.mgmtp.jfunk.core.module.TestModule;


/**
 * {@link InternalBeforeModuleEvent} for internal package-local use only. Is posted before the
 * regular {@link InternalBeforeModuleEvent}.
 * 
 * @author rnaegele
 * @version $Id$
 */
class InternalBeforeModuleEvent extends InternalModuleEvent {

	InternalBeforeModuleEvent(final TestModule module) {
		super(module);
	}
}

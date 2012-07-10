package com.mgmtp.jfunk.core.scripting;

import com.mgmtp.jfunk.core.module.TestModuleImpl;

/**
 * Execution mode for {@link TestModuleImpl}.
 * 
 * @author rnaegele
 * @version $Id$
 */
public enum ExecutionMode {
	/**
	 * Standard mode for executing the whole test module
	 */
	all,

	/**
	 * Execution from the start up to and including the specified break step
	 */
	start,

	/**
	 * Execution from the specified break step until the end
	 */
	finish;
}

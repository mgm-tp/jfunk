package com.mgmtp.jfunk.core.event;

import java.io.File;

/**
 * @author rnaegele
 * @version $Id$
 */
public class AfterScriptEvent extends ScriptEvent {

	private final boolean success;

	public AfterScriptEvent(final File scriptFile, final boolean success) {
		super(scriptFile);
		this.success = success;
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}
}

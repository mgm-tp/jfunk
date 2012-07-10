package com.mgmtp.jfunk.core.event;

import java.io.File;

/**
 * @author rnaegele
 * @version $Id$
 */
public abstract class ScriptEvent extends AbstractBaseEvent {

	private final File scriptFile;

	public ScriptEvent(final File scriptFile) {
		this.scriptFile = scriptFile;
	}

	/**
	 * @return the scriptFile
	 */
	public File getScriptFile() {
		return scriptFile;
	}
}

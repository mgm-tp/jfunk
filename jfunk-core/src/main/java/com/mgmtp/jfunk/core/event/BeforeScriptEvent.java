package com.mgmtp.jfunk.core.event;

import java.io.File;

/**
 * @author rnaegele
 * @version $Id$
 */
public class BeforeScriptEvent extends ScriptEvent {

	public BeforeScriptEvent(final File scriptFile) {
		super(scriptFile);
	}
}

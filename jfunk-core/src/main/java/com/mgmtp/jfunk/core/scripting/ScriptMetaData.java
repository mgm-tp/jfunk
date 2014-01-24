package com.mgmtp.jfunk.core.scripting;

import com.mgmtp.jfunk.common.config.ScriptScoped;

/**
 * Class for holding script meta data.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
@ScriptScoped
public class ScriptMetaData extends ExecutionMetaData {

	private String scriptName;

	/**
	 * @return the scriptName
	 */
	public String getScriptName() {
		return scriptName;
	}

	/**
	 * @param scriptName
	 *            the scriptName to set
	 */
	public void setScriptName(final String scriptName) {
		this.scriptName = scriptName;
	}
}

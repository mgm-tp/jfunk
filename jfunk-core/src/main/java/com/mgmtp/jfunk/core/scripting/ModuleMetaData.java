package com.mgmtp.jfunk.core.scripting;

import com.mgmtp.jfunk.common.config.ModuleScoped;
import com.mgmtp.jfunk.core.module.TestModule;

/**
 * Class for holding module meta data.
 * 
 * @author rnaegele
 * @since 3.1.0
 */
@ModuleScoped
public class ModuleMetaData extends ExecutionMetaData {

	private Class<? extends TestModule> moduleClass;
	private String moduleName;

	/**
	 * @return the moduleClass
	 */
	public Class<? extends TestModule> getModuleClass() {
		return moduleClass;
	}

	/**
	 * @param moduleClass
	 *            the moduleClass to set
	 */
	public void setModuleClass(final Class<? extends TestModule> moduleClass) {
		this.moduleClass = moduleClass;
	}

	/**
	 * @return the moduleName
	 */
	public String getModuleName() {
		return moduleName;
	}

	/**
	 * @param moduleName
	 *            the moduleName to set
	 */
	public void setModuleName(final String moduleName) {
		this.moduleName = moduleName;
	}
}

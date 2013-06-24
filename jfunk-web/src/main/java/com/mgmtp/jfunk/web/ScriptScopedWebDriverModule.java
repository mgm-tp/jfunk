package com.mgmtp.jfunk.web;

import org.openqa.selenium.WebDriver;

import com.mgmtp.jfunk.common.config.ScriptScoped;

/**
 * Guice module for configuring {@link WebDriver} and related classes in script scope.
 * 
 * @author rnaegele
 * @since 3.1
 */
public class ScriptScopedWebDriverModule extends ScopingWebDriverModule {

	public ScriptScopedWebDriverModule() {
		super(ScriptScoped.class);
	}
}

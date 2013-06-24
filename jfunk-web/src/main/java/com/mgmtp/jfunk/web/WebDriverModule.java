package com.mgmtp.jfunk.web;

import org.openqa.selenium.WebDriver;

import com.mgmtp.jfunk.common.config.ModuleScoped;

/**
 * Guice module for configuring {@link WebDriver} and related classes in module scope.
 * 
 * @author rnaegele
 * @since 3.1
 */
public class WebDriverModule extends ScopingWebDriverModule {

	public WebDriverModule() {
		super(ModuleScoped.class);
	}
}

package com.mgmtp.jfunk.web.event;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.script.ScriptEngine;

import org.openqa.selenium.WebDriver;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mgmtp.jfunk.web.util.FormInputHandler;
import com.mgmtp.jfunk.web.util.WebDriverTool;
import com.mgmtp.jfunk.web.util.WebElementFinder;

/**
 * Event handler for making {@link WebDriver}, {@link WebDriverTool}, {@link FormInputHandler}, and
 * {@link WebElementFinder} accessible in Groovy scripts.
 * 
 * @author rnaegele
 * @version $Id: $
 */
@Singleton
public class WebDriverScriptEngineEventHandler {

	private final Provider<WebDriver> webDriverProvider;
	private final Provider<WebDriverTool> webDriverToolProvider;
	private final Provider<FormInputHandler> formInputHandlerProvider;
	private final Provider<WebElementFinder> webElementFinderProvider;

	@Inject
	public WebDriverScriptEngineEventHandler(final Provider<WebDriver> webDriverProvider, final Provider<WebDriverTool> webDriverToolProvider,
			final Provider<FormInputHandler> formInputHandlerProvider, final Provider<WebElementFinder> webElementFinderProvider) {
		this.webDriverProvider = webDriverProvider;
		this.webDriverToolProvider = webDriverToolProvider;
		this.formInputHandlerProvider = formInputHandlerProvider;
		this.webElementFinderProvider = webElementFinderProvider;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void addWebDriverUtilities(final ScriptEngine scriptEngine) {
		scriptEngine.put("webDriver", webDriverProvider.get());
		scriptEngine.put("wdt", webDriverToolProvider.get());
		scriptEngine.put("fih", formInputHandlerProvider.get());
		scriptEngine.put("wef", webElementFinderProvider.get());
	}
}

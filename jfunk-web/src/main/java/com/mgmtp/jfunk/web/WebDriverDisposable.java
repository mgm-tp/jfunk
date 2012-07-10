package com.mgmtp.jfunk.web;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.openqa.selenium.WebDriver;

import com.mgmtp.jfunk.common.util.Disposable;

/**
 * {@link Disposable} for quitting a {@link WebDriver} instance.
 * 
 * @author rnaegele
 * @version $Id$
 */
@Singleton
public class WebDriverDisposable implements Disposable {

	private final Provider<WebDriver> webDriverProvider;

	@Inject
	public WebDriverDisposable(final Provider<WebDriver> webDriverProvider) {
		this.webDriverProvider = webDriverProvider;
	}

	/**
	 * Calls {@link WebDriver#quit()} on the instance return by the provider.
	 */
	@Override
	public void dispose() {
		webDriverProvider.get().quit();
	}

}

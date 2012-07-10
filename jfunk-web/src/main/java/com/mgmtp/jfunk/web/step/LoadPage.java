package com.mgmtp.jfunk.web.step;

import com.mgmtp.jfunk.core.module.TestModule;

/**
 * Load a new web page in the current browser window.
 * 
 * @version $Id$
 */
public class LoadPage extends WebDriverStep {

	private final String url;

	@Deprecated
	public LoadPage(final String url, @SuppressWarnings("unused") final TestModule test) {
		this(url);
	}

	public LoadPage(final String url) {
		this.url = url;
	}

	@Override
	public void execute() {
		String urlString = url;
		log.info("Trying to load page with URL=" + urlString);
		getWebDriver().get(urlString);
	}
}
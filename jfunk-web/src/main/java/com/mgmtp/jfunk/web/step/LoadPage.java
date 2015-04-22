/*
 * Copyright (c) 2015 mgm technology partners GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mgmtp.jfunk.web.step;

import com.mgmtp.jfunk.core.module.TestModule;

/**
 * Load a new web page in the current browser window.
 * 
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
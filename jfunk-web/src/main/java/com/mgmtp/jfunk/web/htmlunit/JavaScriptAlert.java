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
package com.mgmtp.jfunk.web.htmlunit;

import com.gargoylesoftware.htmlunit.Page;

/**
 * Container object for JavaScript alerts.
 * 
 */
public class JavaScriptAlert {
	private final Page page;
	private final String message;

	public JavaScriptAlert(final Page page, final String message) {
		this.page = page;
		this.message = message;
	}

	public Page getPage() {
		return this.page;
	}

	public String getMessage() {
		return this.message;
	}
}
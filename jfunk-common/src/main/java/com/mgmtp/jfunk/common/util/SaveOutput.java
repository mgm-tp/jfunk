/*
 * Copyright (c) 2013 mgm technology partners GmbH
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
package com.mgmtp.jfunk.common.util;

/**
 * Web pages can be saved in different formats. This enum contains all valid types. Right now three
 * types are supported:
 * <ol>
 * <li>HTML (text file)</li>
 * <li>PNG (image file)</li>
 * <li>HTML validation result (text file)</li>
 * </ol>
 * 
 */
public enum SaveOutput {
	/**
	 * Output format for plain HTML
	 */
	HTML("html", true),

	/**
	 * Output format for image (PNG) files ("screenshots")
	 */
	PNG("png", false),

	HTML_VALIDATION("html_validation", "html", false);

	private final String identifier;
	private final String extension;
	private final boolean activeByDefault;

	private SaveOutput(final String identifier, final boolean activeByDefault) {
		this(identifier, identifier, activeByDefault);
	}

	private SaveOutput(final String identifier, final String extension, final boolean activeByDefault) {
		this.identifier = identifier;
		this.extension = extension;
		this.activeByDefault = activeByDefault;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getExtension() {
		return extension;
	}

	public boolean isActiveByDefault() {
		return activeByDefault;
	}
}
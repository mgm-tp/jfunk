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
 * @version $Id$
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
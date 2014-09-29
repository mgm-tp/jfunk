package com.mgmtp.jfunk.core.ui;

/**
 * @author rnaegele
 */
public enum ItemInfoType {
	GROOVY("Groovy Scripts"),
	UNIT_TEST("Test Classes");

	private final String description;

	private ItemInfoType(final String description) {
		this.description = description;
	}
}

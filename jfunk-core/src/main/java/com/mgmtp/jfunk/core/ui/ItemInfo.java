package com.mgmtp.jfunk.core.ui;

import java.nio.file.Path;

/**
 * @author rnaegele
 */
public class ItemInfo {

	private final Path path;
	private final String value;

	public ItemInfo(final Path path) {
		this(null, path);
	}

	public ItemInfo(final String value) {
		this(value, null);
	}

	public ItemInfo(final String value, final Path path) {
		this.path = path;
		this.value = value;
	}

	/**
	 * @return the path
	 */
	public Path getPath() {
		return path;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value != null ? value : path.getFileName().toString();
	}
}

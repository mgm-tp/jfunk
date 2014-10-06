package com.mgmtp.jfunk.application.runner;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.nio.file.Path;

import static org.apache.commons.io.FilenameUtils.removeExtension;

/**
 * @author rnaegele
 */
public class ItemInfo {

	private final Path path;
	private final String value;
	private final ItemInfoType type;

	public ItemInfo(final Path path, final ItemInfoType type) {
		this(path.getFileName().toString(), path, type);
	}

	public ItemInfo(final String value, final ItemInfoType type) {
		this(value, null, type);
	}

	public ItemInfo(final String value, final Path path, final ItemInfoType type) {
		this.path = path;
		this.value = value;
		this.type = type;
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

	public ItemInfoType getType() {
		return type;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}

/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web.util;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.log4j.Logger;

import com.mgmtp.jfunk.common.config.ModuleScoped;

/**
 * @author rnaegele
 */
@ModuleScoped
public class DumpFileCreator {
	// Pattern for replacing illegal file name characters
	private static final Pattern PATTERN_ILLEGAL_CHARS = Pattern.compile("[/?<>\\:*|\"]");

	// Patterns for replacing a leading or trailing slash
	private static final Pattern PATTERN_FIRST_LAST_SLASH = Pattern.compile("^(?:/)?(.*?)(?:/)?$");

	private final Logger log = Logger.getLogger(getClass());

	/**
	 * Contains counters for different output types
	 */
	private final Map<Key, MutableInt> countersMap = newHashMapWithExpectedSize(3);

	/**
	 * Computes the best file to save the response to the current page.
	 */
	public File createDumpFile(final File dir, final String extension, final String urlString, final String additionalInfo) {
		URI uri = URI.create(urlString);
		String path = uri.getPath();
		if (path == null) {
			log.warn("Cannot create dump file for URI: " + uri);
			return null;
		}

		String name = PATTERN_FIRST_LAST_SLASH.matcher(path).replaceAll("$1");
		name = PATTERN_ILLEGAL_CHARS.matcher(name).replaceAll("_");

		Key key = Key.get(dir.getPath(), extension);
		MutableInt counter = countersMap.get(key);
		if (counter == null) {
			counter = new MutableInt();
			countersMap.put(key, counter);
		}

		int counterValue = counter.intValue();
		counter.increment();

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%04d", counterValue));
		sb.append('_');
		sb.append(name);
		if (StringUtils.isNotBlank(additionalInfo)) {
			sb.append("_");
			sb.append(additionalInfo);
		}
		sb.append(".");
		sb.append(extension);

		return new File(dir, sb.toString());
	}

	private static class Key {
		String dirName;
		String extension;

		static Key get(final String dirName, final String extension) {
			Key key = new Key();
			key.dirName = dirName;
			key.extension = extension;
			return key;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (dirName == null ? 0 : dirName.hashCode());
			result = prime * result + (extension == null ? 0 : extension.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Key other = (Key) obj;
			if (dirName == null) {
				if (other.dirName != null) {
					return false;
				}
			} else if (!dirName.equals(other.dirName)) {
				return false;
			}
			if (extension == null) {
				if (other.extension != null) {
					return false;
				}
			} else if (!extension.equals(other.extension)) {
				return false;
			}
			return true;
		}
	}
}

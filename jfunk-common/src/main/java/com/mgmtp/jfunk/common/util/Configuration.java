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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Queues.newArrayDeque;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.exception.JFunkException;

/**
 * {@link ExtendedProperties} subclass adding zip file handling.
 * 
 */
@NotThreadSafe
public final class Configuration extends ExtendedProperties {

	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^},]+)(?:\\s*,[^}]*)?\\}");
	private static final long serialVersionUID = 1L;
	private static final Object STACK_OBJECT = new Object();

	private transient ZipFile zipArchive;

	private final List<String> extraFileProperties = newArrayList();

	private final Charset charset;

	private final Deque<Object> loadingStack = newArrayDeque();

	/**
	 * Creates an empty instance.
	 */
	public Configuration(final Charset charset) {
		this(null, charset);
	}

	/**
	 * Creates an empty instance with the specified defaults.
	 * 
	 * @param defaults
	 *            The defaults
	 */
	public Configuration(final Map<String, String> defaults, final Charset charset) {
		super(defaults);
		this.charset = charset;
	}

	/**
	 * Loads a properties file. If the file is a zip file, the file {@code testing.properties} is
	 * loaded from the zip file root directory. Otherwise,
	 * {@link ResourceLoader#getConfigInputStream(String)} is used to load the file.
	 * 
	 * @param fileName
	 *            The file name
	 */
	public void load(final String fileName) {
		load(fileName, false);
	}

	/**
	 * Loads a properties file. If the file is a zip file, the file {@code testing.properties} is
	 * loaded from the zip file root directory. Otherwise,
	 * {@link ResourceLoader#getConfigInputStream(String)} is used to load the file.
	 * 
	 * @param fileName
	 *            The file name
	 * @param preserveExisting
	 *            If {@code true}, existing properties will not be overwritten from the specified
	 *            properties file.
	 */
	public void load(final String fileName, final boolean preserveExisting) {
		logger.info("Loading config file: {} (preserveExisting={})", fileName, preserveExisting);

		InputStream is = null;
		try {
			loadingStack.push(STACK_OBJECT);
			if (fileName.endsWith(JFunkConstants.ZIP_FILE_SUFFIX)) {
				logger.info("Loading zip archive '{}'...", fileName);
				zipArchive = new ZipFile(fileName);
				ZipEntry entry = zipArchive.getEntry(JFunkConstants.SCRIPT_PROPERTIES);
				if (entry == null) {
					entry = zipArchive.getEntry("script.properties");
				}
				is = zipArchive.getInputStream(entry);
				// keeping preExisting properties is activated with preserveExisting set to true
				doLoad(is, preserveExisting);
			} else {
				is = ResourceLoader.getConfigInputStream(fileName);
				logger.info("Loading file '{}'...", fileName);

				boolean initiallyEmpty = isEmpty();
				doLoad(is, preserveExisting);

				if (initiallyEmpty) {
					// If config was emtpy initially, we add system properties, which always override existing properties.
					// Extra properties are only loaded afterwards but do not override existing properties.
					putAll(ExtendedProperties.fromProperties(System.getProperties()));

					// Archiving is always necessary when execution mode is 'start'.
					// Otherwise continuing after the breakpoint would not be possible.
					if (JFunkConstants.EXECUTION_MODE_START.equals(get(JFunkConstants.EXECUTION_MODE))) {
						put(JFunkConstants.ARCHIVING_MODE, JFunkConstants.ARCHIVING_MODE_ALL);
					}
				}

				loadExtraFiles(JFunkConstants.SYSTEM_PROPERTIES);

				if (initiallyEmpty) {
					// copy SSL settings to System properties
					Properties props = System.getProperties();
					copyProperty(props, this, JFunkConstants.JAVAX_NET_SSL_KEY_STORE_PASSWORD);
					copyProperty(props, this, JFunkConstants.JAVAX_NET_SSL_KEY_STORE);
					copyProperty(props, this, JFunkConstants.JAVAX_NET_SSL_KEY_STORE_TYPE);
					copyProperty(props, this, JFunkConstants.JAVAX_NET_SSL_TRUST_STORE_PASSWORD);
					copyProperty(props, this, JFunkConstants.JAVAX_NET_SSL_TRUST_STORE);
					copyProperty(props, this, JFunkConstants.JAVAX_NET_SSL_TRUST_STORE_TYPE);
				}
			}
		} catch (IOException ex) {
			throw new JFunkException("Error loading properties: " + fileName, ex);
		} finally {
			loadingStack.pop();
			IOUtils.closeQuietly(is);
		}
	}

	private static void copyProperty(final Properties target, final Configuration source, final String key) {
		String value = source.get(key);
		if (!Strings.isNullOrEmpty(value)) {
			target.setProperty(key, value);
		}
	}

	private void doLoad(final InputStream is, final boolean preserveExisting) throws IOException {
		if (!isEmpty() && preserveExisting) {
			Configuration copy = clone();
			super.load(is, charset.name());
			putAll(copy);
		} else {
			super.load(is, charset.name());
		}
	}

	/**
	 * If properties are present which start with {@link JFunkConstants#SYSTEM_PROPERTIES} the
	 * corresponding values are taken as property files and loaded here.
	 */
	private void loadExtraFiles(final String filterPrefix) {
		Map<String, String> view = Maps.filterKeys(this, Predicates.startsWith(filterPrefix));
		while (true) {
			if (view.isEmpty()) {
				break;
			}

			Queue<String> fileKeys = Queues.newArrayDeque(view.values());

			// we need to keep them separately in order to be able to reload them (see put method)
			extraFileProperties.addAll(fileKeys);

			// Remove original keys in order to prevent a stack overflow
			view.clear();

			for (String fileNameKey = null; (fileNameKey = fileKeys.poll()) != null;) {
				// Recursion
				String fileName = processPropertyValue(fileNameKey);
				if (PLACEHOLDER_PATTERN.matcher(fileName).find()) {
					// not all placeholders were resolved, so we enqueue it again to process another file first
					fileKeys.offer(fileName);
				} else {
					// preserve existing keys, so e. g. System properties keep precedence
					load(fileName, true);
				}
			}
		}
	}

	@Override
	public String put(final String key, final String value) {
		String result = super.put(key, value);

		if (loadingStack.isEmpty()) {
			// put is also called during loading, so we need to exclude this re-loading mechanism there,
			// it is not necessary and may lead to errors because not all placeholders might have been resolved yet
			if (!Strings.isNullOrEmpty(value)) {
				// reloads extra files if the contains placeholders and
				// the properties the placeholders refer to are changed
				// --> temp copy of list in order to avoid ConcurrentModificationException
				List<String> tmpExtraFileProps = newArrayList(extraFileProperties);
				for (String extraFile : tmpExtraFileProps) {
					Matcher matcher = PLACEHOLDER_PATTERN.matcher(extraFile);
					while (matcher.find()) {
						if (matcher.group(1).equals(key)) {
							load(processPropertyValue(extraFile));
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Returns an input stream the file with the specified name. If this {@link Configuration} was
	 * loaded from a zip file, and {@link InputStream} to zip entry with the specified name is
	 * return. Otherwise, {@link ResourceLoader#getInputStream(String)} is used.
	 * 
	 * @return The stream
	 */
	public InputStream openStream(final String filename) throws IOException {
		if (zipArchive != null) {
			// lade aus zip datei
			ZipEntry entry = null;
			int index = 0;
			String name = filename;
			do {
				name = name.substring(index);
				entry = zipArchive.getEntry(name);
				index = name.indexOf('/') + 1;
				if (index == 0) {
					index = name.indexOf('\\') + 1;
				}
			} while (entry == null && index > 0);
			if (entry != null) {
				logger.info("Opening stream to '{}' in zip archive...", name);
				return zipArchive.getInputStream(entry);
			}
		}
		return ResourceLoader.getInputStream(filename);
	}

	/**
	 * Extracts the specified file from this configuration's zip file, if applicable.
	 * 
	 * @param targetFile
	 *            The target file. The file name without the path information is taken in order to
	 *            identify the zip entry to extract.
	 * @param forceOverwrite
	 *            If {@code true}, an existing file is overwritten.
	 */
	public void extractFromArchive(final File targetFile, final boolean forceOverwrite) throws IOException {
		if (zipArchive != null) {
			ZipEntry entry = zipArchive.getEntry(targetFile.getName());
			if (entry != null) {
				if (!targetFile.exists()) {
					try {
						targetFile.createNewFile();
					} catch (IOException ex) {
						throw new JFunkException("Error creating file: " + targetFile, ex);
					}
				} else if (!forceOverwrite) {
					return;
				}
				logger.info("Loading file '{}' from zip archive...", targetFile);
				OutputStream out = null;
				InputStream in = null;
				try {
					out = new FileOutputStream(targetFile);
					in = zipArchive.getInputStream(entry);
					IOUtils.copy(in, out);
				} finally {
					IOUtils.closeQuietly(in);
					IOUtils.closeQuietly(out);
				}
			} else {
				logger.error("Could not find file '{}' in zip archive", targetFile);
			}
		}
	}

	@Override
	public Configuration clone() {
		return (Configuration) super.clone();
	}

	/**
	 * @serialData The name of the zip file.
	 */
	private void writeObject(final ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		oos.writeUTF(zipArchive.getName());
	}

	private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		String zipFileName = ois.readUTF();
		zipArchive = new ZipFile(zipFileName);
	}
}
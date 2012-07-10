package com.mgmtp.jfunk.common.util;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.exception.JFunkException;

/**
 * {@link ExtendedProperties} subclass adding zip file handling.
 * 
 * @version $Id$
 */
@NotThreadSafe
public final class Configuration extends ExtendedProperties {

	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^},]+)(?:\\s*,[^}]*)?\\}");

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(ExtendedProperties.class);

	private transient ZipFile zipArchive;

	private final Map<String, String> extraFileProperties = newHashMapWithExpectedSize(5);

	private final Charset charset;

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
		InputStream is = null;
		try {
			if (fileName.endsWith(JFunkConstants.ZIP_FILE_SUFFIX)) {
				log.info("Loading zip archive '" + fileName + "'...");
				zipArchive = new ZipFile(fileName);
				ZipEntry entry = zipArchive.getEntry(JFunkConstants.SCRIPT_PROPERTIES);
				if (entry == null) {
					entry = zipArchive.getEntry("testing.properties");
				}
				is = zipArchive.getInputStream(entry);
				// keeping preExisting properties is activated with preserveExisting set to true
				doLoad(is, preserveExisting);
			} else {
				is = ResourceLoader.getConfigInputStream(fileName);
				log.info("Loading file '" + fileName + "'...");
				doLoad(is, preserveExisting);
				loadExtraFiles(JFunkConstants.SYSTEM_PROPERTIES);
			}
		} catch (IOException ex) {
			throw new JFunkException("Error loading properties: " + fileName, ex);
		} finally {
			IOUtils.closeQuietly(is);
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
		while (true) {
			Map<String, String> view = Maps.filterKeys(this, Predicates.startsWithPredicate(filterPrefix));

			// we need to keep them separately in order to be able to reload them (see put method)
			extraFileProperties.putAll(view);

			if (view.isEmpty()) {
				break;
			}

			// We need a copy because we only have a view, so we can remove the original keys.
			Map<String, String> copy = Maps.newHashMap(view);

			// Remove original keys in order to prevent a stack overflow
			view.clear();

			for (String fileName : copy.values()) {
				// Recursion
				load(processPropertyValue(fileName));
			}
		}
	}

	@Override
	public String put(final String key, final String value) {
		String result = super.put(key, value);

		if (!Strings.isNullOrEmpty(value)) {
			// reloads extra files if the contains placeholders and
			// the properties the placeholders refer to are changed
			for (String extraFile : extraFileProperties.values()) {
				Matcher matcher = PLACEHOLDER_PATTERN.matcher(extraFile);
				while (matcher.find()) {
					if (matcher.group(1).equals(key)) {
						load(processPropertyValue(extraFile));
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
				log.info("Opening stream to '" + name + "' in zip archive...");
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
				log.info("Loading file '" + targetFile + "' from zip archive...");
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
				log.info("Could not find file '" + targetFile + "' in zip archive");
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
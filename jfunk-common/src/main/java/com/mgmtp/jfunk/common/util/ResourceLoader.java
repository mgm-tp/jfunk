package com.mgmtp.jfunk.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

/**
 * <p>
 * Loads resources relative to a specified directory.
 * </p>
 * <p>
 * Resources are first loaded from the file systems. If this fails, resources are loaded from the
 * classpath.
 * </p>
 * 
 * @version $Id$
 */
public final class ResourceLoader {

	private static final Logger LOG = Logger.getLogger(ClassLoader.class);

	private ResourceLoader() {
		// don't allow instantiation
	}

	/**
	 * Defaults to the directory "config" relative to the working directory. May be overridden by
	 * the system property "config.dir".
	 */
	public static final String getConfigDir() {
		return System.getProperty("config.dir", "config");
	}

	/**
	 * Loads a resource as {@link BufferedReader} using the default encoding.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @return The reader
	 */
	public static BufferedReader getBufferedReader(final File resource) throws IOException {
		return new BufferedReader(getInputStreamReader(resource));
	}

	/**
	 * Loads a resource as {@link BufferedReader}.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @param encoding
	 *            The encoding to use
	 * @return The reader
	 */
	public static BufferedReader getBufferedReader(final File resource, final String encoding) throws IOException {
		return new BufferedReader(getInputStreamReader(resource, encoding));
	}

	/**
	 * Loads a resource as {@link BufferedReader} using the default encoding.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @return The reader
	 */
	public static BufferedReader getBufferedReader(final String resource) throws IOException {
		return new BufferedReader(getInputStreamReader(resource));
	}

	/**
	 * Loads a resource as {@link BufferedReader}.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @param encoding
	 *            The encoding to use
	 * @return The reader
	 */
	public static BufferedReader getBufferedReader(final String resource, final String encoding) throws IOException {
		return new BufferedReader(getInputStreamReader(resource, encoding));
	}

	/**
	 * Loads a resource as {@link BufferedReader} relative {@link #getConfigDir()} using the default
	 * encoding.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @return The reader
	 */
	public static BufferedReader getConfigBufferedReader(final String resource) throws IOException {
		return new BufferedReader(getConfigInputStreamReader(resource));
	}

	/**
	 * Loads a resource as {@link BufferedReader} relative {@link #getConfigDir()}.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @param encoding
	 *            The encoding to use
	 * @return The reader
	 */
	public static BufferedReader getConfigBufferedReader(final String resource, final String encoding) throws IOException {
		return new BufferedReader(getConfigInputStreamReader(resource, encoding));
	}

	/**
	 * Creates a {@link File} object relative to {@link #getConfigDir()}.
	 * 
	 * @param file
	 *            The relative path starting from {@link #getConfigDir()}
	 * @return The file
	 */
	public static File getConfigFile(final String file) {
		return new File(getConfigDir(), file);
	}

	/**
	 * Loads a resource as {@link InputStream} relative to {@link #getConfigDir()}.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @return The stream
	 */
	public static InputStream getConfigInputStream(final String resource) throws IOException {
		return getInputStream(getConfigDir(), resource);
	}

	/**
	 * Loads a resource as {@link InputStreamReader} relative to {@link #getConfigDir()} using the
	 * default encoding.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @return The reader
	 */
	public static InputStreamReader getConfigInputStreamReader(final String resource) throws IOException {
		return new InputStreamReader(getConfigInputStream(resource));
	}

	/**
	 * Loads a resource as {@link InputStreamReader} relative to {@link #getConfigDir()}.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @param encoding
	 *            The encoding to use
	 * @return The reader
	 */
	public static InputStreamReader getConfigInputStreamReader(final String resource, final String encoding) throws IOException {
		return new InputStreamReader(getConfigInputStream(resource), encoding);
	}

	/**
	 * Loads a resource as {@link InputStream}.
	 * 
	 * @param resource
	 *            The resource to be loaded. If {@code baseDir} is not {@code null}, it is loaded
	 *            relative to {@code baseDir}.
	 * @return The stream
	 */
	public static InputStream getInputStream(final File resource) throws IOException {
		if (resource.exists()) {
			return new FileInputStream(resource);
		}
		LOG.info("Could not find file '" + resource.getAbsolutePath() + "' in the file system. Trying to load it from the classpath...");

		String path = FilenameUtils.separatorsToUnix(resource.getPath());
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		InputStream is = cl.getResourceAsStream(path);

		if (is == null) {
			if (path.startsWith("/")) {
				LOG.info("Could not find file '" + resource
						+ " in the file system. Trying to load it from the classpath without the leading slash...");
				is = cl.getResourceAsStream(path.substring(1));
			}

			if (is == null) {
				// If configs are to be loaded from the classpath, we also try it directly
				// stripping of the config directory
				String configDir = new File(getConfigDir()).getName();
				if (path.startsWith(configDir)) {
					is = cl.getResourceAsStream(StringUtils.substringAfter(path, configDir + '/'));
				}
			}

			if (is == null) {
				throw new FileNotFoundException("Could not load file '" + resource + "'");
			}
		}

		return is;
	}

	/**
	 * Loads a resource as {@link InputStream}.
	 * 
	 * @param baseDir
	 *            If not {@code null}, the directory relative to which resources are loaded.
	 * @param resource
	 *            The resource to be loaded. If {@code baseDir} is not {@code null}, it is loaded
	 *            relative to {@code baseDir}.
	 * @return The stream
	 */
	public static InputStream getInputStream(final File baseDir, final String resource) throws IOException {
		File resourceFile = baseDir == null ? new File(resource) : new File(baseDir, resource);
		return getInputStream(resourceFile);
	}

	/**
	 * Loads a resource as {@link InputStream}.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @return The stream
	 */
	public static InputStream getInputStream(final String resource) throws IOException {
		return getInputStream((File) null, resource);
	}

	/**
	 * Loads a resource as {@link InputStream}.
	 * 
	 * @param baseDir
	 *            If not {@code null}, the directory relative to which resources are loaded.
	 * @param resource
	 *            The resource to be loaded. If {@code baseDir} is not {@code null}, it is loaded
	 *            relative to {@code baseDir}.
	 * @return The stream
	 */
	public static InputStream getInputStream(final String baseDir, final String resource) throws IOException {
		return getInputStream(new File(baseDir), resource);
	}

	/**
	 * Loads a resource as {@link InputStreamReader} using the default encoding.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @return The reader
	 */
	public static InputStreamReader getInputStreamReader(final File resource) throws IOException {
		return new InputStreamReader(getInputStream(resource));
	}

	/**
	 * Loads a resource as {@link InputStreamReader}.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @param encoding
	 *            The encoding to use
	 * @return The reader
	 */
	public static InputStreamReader getInputStreamReader(final File resource, final String encoding) throws IOException {
		return new InputStreamReader(getInputStream(resource), encoding);
	}

	/**
	 * Loads a resource as {@link InputStreamReader} using the default encoding.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @return The reader
	 */
	public static InputStreamReader getInputStreamReader(final String resource) throws IOException {
		return new InputStreamReader(getInputStream(resource));
	}

	/**
	 * Loads a resource as {@link InputStreamReader}.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @param encoding
	 *            The encoding to use
	 * @return The reader
	 */
	public static InputStreamReader getInputStreamReader(final String resource, final String encoding) throws IOException {
		return new InputStreamReader(getInputStream(resource), encoding);
	}

	/**
	 * Loads a resource relative {@link #getConfigDir()} into a {@link List} of strings, each entry
	 * representing one line.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @return The list
	 */
	public static List<String> readConfigIntoLines(final String resource) throws IOException {
		BufferedReader br = null;
		try {
			br = ResourceLoader.getConfigBufferedReader(resource);
			return doReadIntoLines(br);
		} finally {
			IOUtils.closeQuietly(br);
		}
	}

	/**
	 * Loads a resource relative {@link #getConfigDir()} into a {@link List} of strings, each entry
	 * representing one line.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @param encoding
	 *            The encoding to use
	 * @return The list
	 */
	public static List<String> readConfigIntoLines(final String resource, final String encoding) throws IOException {
		BufferedReader br = null;
		try {
			br = ResourceLoader.getConfigBufferedReader(resource, encoding);
			return doReadIntoLines(br);
		} finally {
			IOUtils.closeQuietly(br);
		}
	}

	/**
	 * Loads a resource into a {@link List} of strings, each entry representing one line.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @return The list
	 */
	public static List<String> readIntoLines(final File resource) throws IOException {
		BufferedReader br = null;
		try {
			br = ResourceLoader.getBufferedReader(resource);
			return doReadIntoLines(br);
		} finally {
			IOUtils.closeQuietly(br);
		}
	}

	/**
	 * Loads a resource into a {@link List} of strings, each entry representing one line.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @param encoding
	 *            The encoding to use
	 * @return The list
	 */
	public static List<String> readIntoLines(final File resource, final String encoding) throws IOException {
		BufferedReader br = null;
		try {
			br = ResourceLoader.getBufferedReader(resource, encoding);
			return doReadIntoLines(br);
		} finally {
			IOUtils.closeQuietly(br);
		}
	}

	/**
	 * Loads a resource into a {@link List} of strings, each entry representing one line.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @return The list
	 */
	public static List<String> readIntoLines(final String resource) throws IOException {
		BufferedReader br = null;
		try {
			br = ResourceLoader.getBufferedReader(resource);
			return doReadIntoLines(br);
		} finally {
			IOUtils.closeQuietly(br);
		}
	}

	/**
	 * Loads a resource into a {@link List} of strings, each entry representing one line.
	 * 
	 * @param resource
	 *            The resource to be loaded.
	 * @param encoding
	 *            The encoding to use
	 * @return The list
	 */
	public static List<String> readIntoLines(final String resource, final String encoding) throws IOException {
		BufferedReader br = null;
		try {
			br = ResourceLoader.getBufferedReader(resource, encoding);
			return doReadIntoLines(br);
		} finally {
			IOUtils.closeQuietly(br);
		}
	}

	private static List<String> doReadIntoLines(final BufferedReader br) throws IOException {
		List<String> lines = Lists.newArrayList();
		for (String currentLine = null; (currentLine = br.readLine()) != null;) {
			lines.add(currentLine);
		}
		return lines;
	}
}

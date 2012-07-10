package com.mgmtp.jfunk.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.mgmtp.jfunk.common.exception.JFunkException;

/**
 * Extended {@link File} class.
 * <ul>
 * <li>Upon creation it is possible to create all necessary but nonexistent parent directories.</li>
 * <li>Upon deletion all included files and directories will be deleted recursively before the file
 * itself is deleted</li>
 * <li>Can zip itself to a zip file</li>
 * </ul>
 * 
 * @version $Id$
 */
public class ExtendedFile extends File {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(ExtendedFile.class);

	/**
	 * Creates a new {@code ExtendedFile} instance from a parent abstract pathname and a child
	 * pathname string.
	 * 
	 * @param makedir
	 *            if {@code true} all necessary but nonexistent parent directories are created
	 */
	public ExtendedFile(final File parent, final String child, final boolean makedir) {
		super(parent, child);
		if (!getParentFile().exists() && makedir && !getParentFile().mkdirs()) {
			throw new JFunkException("Could not create directory " + getParentFile().getAbsolutePath());
		}
	}

	/**
	 * Creates a new {@code ExtendedFile} instance. All necessary but nonexistent parent directories
	 * are created.
	 * 
	 * @see #ExtendedFile(String, boolean)
	 */
	public ExtendedFile(final String pathname) {
		this(pathname, true);
	}

	/**
	 * Creates a new {@code ExtendedFile} instance.
	 * 
	 * @param makedir
	 *            if {@code true} all necessary but nonexistent parent directories are created
	 */
	public ExtendedFile(final String filename, final boolean makedir) {
		super(filename);
		if (getParentFile() != null && !getParentFile().exists() && makedir && !getParentFile().mkdirs()) {
			throw new JFunkException("Could not create directory " + getParentFile().getAbsolutePath());
		}
	}

	/**
	 * Creates a new {@code ExtendedFile} instance from a parent pathname string and a child
	 * pathname string.
	 * 
	 * @param makedir
	 *            if {@code true} all necessary but nonexistent parent directories are created
	 */
	public ExtendedFile(final String parent, final String child, final boolean makedir) {
		super(parent, child);
		if (!getParentFile().exists() && makedir && !getParentFile().mkdirs()) {
			throw new JFunkException("Could not create directory " + getParentFile().getAbsolutePath());
		}
	}

	/**
	 * Copies the specified file. If this object is a directory the file will be copied to this
	 * directory. If this object represents a file it will be overwritten with the specified file.
	 */
	public void copy(final File source) throws IOException {
		if (!source.exists()) {
			LOG.warn("File " + source + " cannot be copied as it does not exist");
			return;
		}
		if (equals(source)) {
			LOG.info("Skipping copying of " + source + " as it matches the target");
			return;
		}
		File target = isDirectory() ? new File(this, source.getName()) : this;
		FileUtils.copyFile(source, target);
	}

	/**
	 * Deletes the file. If the file represents a directory all its contents will be deleted first.
	 * Whenever the deletion for a single object fails it will be marked for deletion upon exit.
	 * Therefore this method always returns {@code true}.
	 * 
	 * @return always true
	 * @see File#deleteOnExit()
	 */
	@Override
	public boolean delete() {
		LOG.debug("Deleting " + this);
		delete(this);
		return true;
	}

	/**
	 * Zips all included objects into the specified file.
	 * 
	 * @param zipFile
	 *            the zip file to be created
	 */
	public void zip(final File zipFile) throws IOException {
		File[] files = listFiles();
		if (files.length == 0) {
			return;
		}
		LOG.info("Creating zip file " + zipFile + " from directory " + this);
		ZipOutputStream zipOut = null;
		try {
			zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
			for (File file : files) {
				zip("", file, zipOut);
			}
		} finally {
			IOUtils.closeQuietly(zipOut);
		}
	}

	private void delete(final File f) {
		if (f.isDirectory()) {
			File[] childs = f.listFiles();
			for (File c : childs) {
				delete(c);
			}
		}
		// Avoid endless recursion
		if (f == this) {
			if (!super.delete()) {
				LOG.warn("Could not delete " + this + ". Will be marked for deletion on exit.");
				super.deleteOnExit();
			}
			return;
		}
		if (!f.delete()) {
			LOG.warn("Could not delete " + f + ". Will be marked for deletion on exit.");
			f.deleteOnExit();
		}
	}

	private void zip(String prefix, final File file, final ZipOutputStream zipOut) throws IOException {
		if (file.isDirectory()) {
			prefix = prefix + file.getName() + '/';
			for (File child : file.listFiles()) {
				zip(prefix, child, zipOut);
			}
		} else {
			FileInputStream in = null;
			try {
				in = new FileInputStream(file);
				zipOut.putNextEntry(new ZipEntry(prefix + file.getName()));
				IOUtils.copy(in, zipOut);
			} finally {
				IOUtils.closeQuietly(in);
				zipOut.flush();
				zipOut.closeEntry();
			}
		}
	}
}
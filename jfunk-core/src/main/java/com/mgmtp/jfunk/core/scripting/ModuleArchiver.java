/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.scripting;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.apache.commons.io.FileUtils.copyFileToDirectory;
import static org.apache.commons.io.FileUtils.deleteQuietly;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.Format;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.config.ModuleScoped;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.common.util.ExtendedProperties;
import com.mgmtp.jfunk.core.config.ArchiveDir;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.data.DataSet;
import com.mgmtp.jfunk.data.source.DataSource;

/**
 * Handles archiving of {@link TestModule}s. There are three different archiving modes:
 * <ul>
 * <li>{@link JFunkConstants#ARCHIVING_MODE_ALL ALL} - archiving happens always, i. e an archive
 * directory is created and zipped up after a {@link TestModule} has finished</li>
 * <li>{@link JFunkConstants#ARCHIVING_MODE_ERROR ERROR} - archiving happens during the execution of
 * a {@link TestModule}, but the archive is only zipped up when an eror occurs and otherwise deleted
 * </li>
 * <li>{@link JFunkConstants#ARCHIVING_MODE_NONE NONE} - no archiving happens at all</li>
 * </ul>
 * 
 * @author rnaegele
 * @version $Id$
 */
@ModuleScoped
public class ModuleArchiver {
	private static final Format FORMAT = FastDateFormat.getInstance("yyyyMMdd_HHmmss,SSS", Locale.GERMANY);
	private static final String DIR_PATTERN = "%s_%s_[%s]";

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Configuration configuration;
	private final DataSource dataSource;

	private final File archiveDir;
	private final Charset charset;

	private File moduleArchiveDir;

	private FileAppender<ILoggingEvent> moduleAppender;

	/**
	 * Creates a new instance.
	 * 
	 * @param configuration
	 *            the configuration instance
	 * @param dataSource
	 *            the dats source
	 * @param archiveDir
	 *            the archive directory
	 */
	@Inject
	ModuleArchiver(final Configuration configuration, final DataSource dataSource, @ArchiveDir final File archiveDir, final Charset charset) {
		this.configuration = configuration;
		this.dataSource = dataSource;
		this.archiveDir = archiveDir;
		this.charset = charset;
	}

	/**
	 * Starts archiving of the specified module, if archiving is enabled.
	 * 
	 * @param testModule
	 *            the test module
	 */
	void startArchiving(final TestModule testModule) {
		if (isArchivingDisabled()) {
			return;
		}

		String archiveName = configuration.get(JFunkConstants.ARCHIVE_FILE);
		if (StringUtils.isBlank(archiveName)) {
			archiveName = String.format(DIR_PATTERN, testModule.getName(), FORMAT.format(new Date()), Thread.currentThread().getName());
		}

		moduleArchiveDir = new File(archiveDir, archiveName);
		if (moduleArchiveDir.exists()) {
			log.warn("Archive directory already exists: {}", moduleArchiveDir);
		} else {
			moduleArchiveDir.mkdirs();
		}

		addModuleAppender();
		log.info("Started archiving: (module={}, moduleArchiveDir={})", testModule, moduleArchiveDir);
	}

	private void addModuleAppender() {
		final Thread thread = Thread.currentThread();

		moduleAppender = new FileAppender<ILoggingEvent>() {
			@Override
			protected void subAppend(final ILoggingEvent event) {
				if (thread.equals(Thread.currentThread())) {
					super.subAppend(event);
				}
			}
		};

		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		moduleAppender.setContext(loggerContext);
		moduleAppender.setFile(new File(moduleArchiveDir, "module.log").getPath());

		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(loggerContext);
		encoder.setPattern("%date{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{35} - %msg%n");
		encoder.start();

		moduleAppender.setEncoder(encoder);
		moduleAppender.start();

		// attach the rolling file appender to the logger of your choice
		ch.qos.logback.classic.Logger logbackLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
		logbackLogger.addAppender(moduleAppender);
	}

	void finishArchiving(final TestModule testModule, final Throwable throwable) {
		boolean success = !testModule.isError() && throwable == null;
		try {
			if (isArchivingDisabled()) {
				return;
			}
			if (JFunkConstants.ARCHIVING_MODE_ERROR.equals(configuration.get(JFunkConstants.ARCHIVING_MODE)) && success) {
				return;
			}

			Configuration configClone = configuration.clone();
			configClone.put(JFunkConstants.CURRENT_MODULE_NAME, testModule.getName());
			configClone.put(JFunkConstants.CURRENT_MODULE_RESULT, success ? JFunkConstants.OK : JFunkConstants.ERROR);
			configClone.put(JFunkConstants.TESTMODULE_CLASS, testModule.getClass().getName());

			saveDataSets(configClone);
			saveConfiguration(configClone);
			saveStackTrace(throwable);

			zipUpArchive(success);

			log.info("Finished archiving: (module={}, moduleArchiveDir={})", testModule, moduleArchiveDir);
		} finally {
			if (moduleAppender != null) {
				LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
				moduleAppender.stop();
				loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).detachAppender(moduleAppender);
			}
			deleteQuietly(moduleArchiveDir);
		}
	}

	/**
	 * Gets the module's archive directory.
	 * 
	 * @return the moduleArchiveDir
	 */
	public File getModuleArchiveDir() {
		return moduleArchiveDir;
	}

	/**
	 * Adds a file to the archive directory if it is not already present in the archive directory.
	 * 
	 * @param fileOrDirToAdd
	 *            the file or directory to add
	 * @throws IOException
	 *             if an IO error occurs during copying
	 */
	public void addToArchive(final File fileOrDirToAdd) throws IOException {
		addToArchive(new File("."), fileOrDirToAdd);
	}

	/**
	 * Adds a file or directory (recursively) to the archive directory if it is not already present
	 * in the archive directory.
	 * 
	 * @param relativeDir
	 *            the directory relative to the archive root directory which the specified file or
	 *            directory is added to
	 * @param fileOrDirToAdd
	 *            the file or directory to add
	 * @throws IOException
	 *             if an IO error occurs during copying
	 */
	public void addToArchive(final File relativeDir, final File fileOrDirToAdd) throws IOException {
		checkArgument(!relativeDir.isAbsolute(), "'relativeDir' must be a relative directory");

		if (isArchivingDisabled()) {
			return;
		}

		if (fileOrDirToAdd.getCanonicalPath().startsWith(moduleArchiveDir.getCanonicalPath())) {
			// File already in archive dir
			log.info("File '" + fileOrDirToAdd + "' already in archive directory. No copying necessary.");
			return;
		}

		File archiveTargetDir = new File(moduleArchiveDir, relativeDir.getPath());
		if (fileOrDirToAdd.isDirectory()) {
			copyDirectory(fileOrDirToAdd, archiveTargetDir);
		} else {
			copyFileToDirectory(fileOrDirToAdd, archiveTargetDir);
		}
	}

	private void saveConfiguration(final Configuration config) {
		/*
		 * If the execution mode is set to "start" it will be set to "finish" so that the archived
		 * run will be continued upon the next execution.
		 */
		if (JFunkConstants.EXECUTION_MODE_START.equals(config.get(JFunkConstants.EXECUTION_MODE))) {
			config.put(JFunkConstants.EXECUTION_MODE, JFunkConstants.EXECUTION_MODE_FINISH);
		}

		File f = new File(moduleArchiveDir, JFunkConstants.SCRIPT_PROPERTIES);
		Writer out = null;
		try {
			out = Files.newWriter(f, charset);
			config.store(out, "Archived testing properties", true, false);
		} catch (IOException e) {
			throw new JFunkException("Could not write testing properties " + f.getName(), e);
		} finally {
			Closeables.closeQuietly(out);
		}
	}

	public boolean isArchivingDisabled() {
		return JFunkConstants.ARCHIVING_MODE_NONE.equals(configuration.get(JFunkConstants.ARCHIVING_MODE));
	}

	private void saveDataSets(final Configuration config) {
		File dir = new File(moduleArchiveDir, "formdata");
		checkState(dir.mkdir(), "Could not create directory " + dir.getPath() + " in archive dir " + this);

		for (Entry<String, DataSet> entry : dataSource.getCurrentDataSets().entrySet()) {
			DataSet data = entry.getValue();
			// May be null, if configured in script.properties but no data
			// available for the key.
			if (data != null) {
				File f = new File(dir, entry.getKey() + JFunkConstants.FORM_PROPERTIES_ENDING);
				config.put(JFunkConstants.FORM_DATA_PREFIX + entry.getKey() + JFunkConstants.PROPERTIES_ENDING, "formdata/" + f.getName());
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(f);
					ExtendedProperties p = new ExtendedProperties(data.getDataView());
					p.store(out, "Archived form data", true, false);
				} catch (IOException ex) {
					throw new JFunkException("Could not write FormData properties " + f.getName(), ex);
				} finally {
					Closeables.closeQuietly(out);
				}
			}
		}
	}

	private void saveStackTrace(final Throwable throwable) {
		if (throwable != null) {
			PrintWriter pr = null;
			try {
				pr = new PrintWriter(new File(moduleArchiveDir, JFunkConstants.STACKTRACE_LOG), "UTF-8");
				throwable.printStackTrace(pr);
			} catch (IOException ex) {
				throw new JFunkException("Error writing stacktrace log to archive.", ex);
			} finally {
				Closeables.closeQuietly(pr);
			}
		}
	}

	private void zipUpArchive(final boolean success) {
		File[] files = moduleArchiveDir.listFiles();
		if (files.length == 0) {
			return;
		}

		File zipFile = new File(moduleArchiveDir.getParentFile(), moduleArchiveDir.getName() + (success ? "_ok.zip" : "_error.zip"));
		log.info("Creating zip file " + zipFile + " from directory " + this);

		ZipOutputStream zipOut = null;
		try {
			zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
			for (File file : files) {
				zip("", file, zipOut);
			}
		} catch (IOException ex) {
			throw new JFunkException("Error creating archive zip: " + zipFile, ex);
		} finally {
			Closeables.closeQuietly(zipOut);
		}
	}

	private void zip(final String prefix, final File file, final ZipOutputStream zipOut) throws IOException {
		if (file.isDirectory()) {
			String recursePrefix = prefix + file.getName() + '/';
			for (File child : file.listFiles()) {
				zip(recursePrefix, child, zipOut);
			}
		} else {
			FileInputStream in = null;
			try {
				in = new FileInputStream(file);
				zipOut.putNextEntry(new ZipEntry(prefix + file.getName()));
				IOUtils.copy(in, zipOut);
			} finally {
				Closeables.closeQuietly(in);
				zipOut.flush();
				zipOut.closeEntry();
			}
		}
	}
}

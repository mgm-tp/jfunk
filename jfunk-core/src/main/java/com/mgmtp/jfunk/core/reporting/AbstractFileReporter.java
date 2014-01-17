/*
 * Copyright (c) 2014 mgm technology partners GmbH
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
package com.mgmtp.jfunk.core.reporting;

import static com.google.common.collect.Lists.newLinkedList;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Deque;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.core.config.ArchiveDir;

/**
 * <p>
 * Base class for reporters that write a report file.
 * </p>
 * <p>
 * Requires members injection.
 * </p>
 * 
 * @author rnaegele
 */
@ThreadSafe
public abstract class AbstractFileReporter implements Reporter {

	protected Logger log = LoggerFactory.getLogger(getClass());

	protected final Charset charset;
	protected final String fileName;
	protected final Deque<String> reportLines = newLinkedList();

	@Inject
	@ArchiveDir
	protected Provider<File> archiveDir;

	@Inject
	protected Charset defaultCharset;

	@Inject
	protected Provider<Configuration> configProvider;

	/**
	 * @param charset
	 *            the charset to use for writing the report file; if {@code null}, the default
	 *            charset is used
	 */
	public AbstractFileReporter(final String fileName, final Charset charset) {
		this.fileName = fileName;
		this.charset = charset;
	}

	/**
	 * Gets the default file extension for the report file, which is used if no file name is
	 * specified.
	 * 
	 * @return the default file extension
	 */
	protected abstract String getFileExtension();

	/**
	 * Gets the header line for the report.
	 * 
	 * @return the header line; if {@code null}, no header line is written
	 */
	protected String getHeaderLine() {
		return null;
	}

	/**
	 * Writes the report to a file. The report is written to the archive direcory as specified by
	 * the property {@link JFunkConstants#ARCHIVE_DIR}.
	 */
	@Override
	public synchronized void createReport() throws IOException {
		if (reportLines.isEmpty()) {
			log.debug("No data to report. Skipping report generation.");
			return;
		}

		File reportFile = new File(archiveDir.get(), createFileName());

		log.debug("Writing report to file: {}", reportFile);

		String header = getHeaderLine();
		if (header != null) {
			reportLines.addFirst(header);
		}
		FileUtils.writeLines(reportFile, charset == null ? defaultCharset.name() : charset.name(), reportLines);

		reportLines.clear();
	}

	/**
	 * <p>
	 * Creates the file name for the report file. The following logic is applied:
	 * </p>
	 * <p>
	 * If a file name was passed to the constructor:<br />
	 * {@code <base name of the file name>-[<thread name>].<extension of the file name>}, e. g.
	 * {@code path/to/archivedir/myreport-[01].csv}
	 * </p>
	 * <p>
	 * If {@code null} was passed to the constructor as file name:<br >
	 * {@code <reporter's name>-<script base name or test method name>-[<thread name>].<reporter's file extension>}
	 * </p>
	 * 
	 * @return the report file name
	 */
	protected synchronized String createFileName() {
		String threadName = Thread.currentThread().getName();

		if (fileName != null) {
			String baseName = getBaseName(fileName);
			String extension = getExtension(fileName);
			return baseName + "-[" + threadName + "]." + extension;
		}

		Configuration config = configProvider.get();

		String id = config.get(JFunkConstants.SCRIPT_NAME);
		if (isNotBlank(id)) {
			id = substringBeforeLast(id, "."); // strip .groovy extension
		} else {
			id = config.get(JFunkConstants.UNIT_TEST_METHOD);
		}

		return getName() + "-" + id + "-[" + threadName + "]." + getFileExtension();
	}
}
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
package com.mgmtp.jfunk.core.step;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.mgmtp.jfunk.core.exception.StepException;
import com.mgmtp.jfunk.core.scripting.ModuleArchiver;
import com.mgmtp.jfunk.core.step.base.BaseStep;

/**
 * Adds a file to the test archive.
 * 
 */
public class AddFile2Archive extends BaseStep {

	public static final Logger LOG = Logger.getLogger(AddFile2Archive.class);

	private final File file;

	@Inject
	ModuleArchiver archiver;

	/**
	 * Creates a new instance of AddFile2Archive.
	 * 
	 * @param fileName
	 *            the name of the file to be added to the archive directory
	 */
	public AddFile2Archive(final String fileName) {
		this(new File(fileName));
	}

	/**
	 * Creates a new instance of AddFile2Archive.
	 * 
	 * @param file
	 *            the file to be added to the archive directory
	 */
	public AddFile2Archive(final File file) {
		this.file = file;
	}

	@Override
	public void execute() {
		LOG.info("Adding file " + file + " to test archive");
		if (!file.exists()) {
			LOG.warn(file + " does not exist and thus could not be added to the test archive");
		} else {
			try {
				archiver.addToArchive(file);
				LOG.debug("Added file " + file + " to test archive");
			} catch (IOException ex) {
				throw new StepException("Error adding file to archive: " + file, ex);
			}
		}
	}
}
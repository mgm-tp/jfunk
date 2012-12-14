/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.step;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.mgmtp.jfunk.core.exception.StepException;
import com.mgmtp.jfunk.core.module.TestModule;
import com.mgmtp.jfunk.core.scripting.ModuleArchiver;
import com.mgmtp.jfunk.core.step.base.BaseStep;

/**
 * Adds a file to the test archive.
 * 
 * @version $Id$
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

	/**
	 * Creates a new instance of AddFile2Archive.
	 * 
	 * @param fileName
	 *            the name of the file to be added to the archive directory
	 * @param testModule
	 *            param no longer used
	 */
	@Deprecated
	public AddFile2Archive(final String fileName, final TestModule testModule) {
		this(fileName);
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
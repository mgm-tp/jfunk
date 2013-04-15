/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.server.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Container class for {@link FileSystemItem} instances which facilitates JAXB handling.
 * 
 * @author rnaegele
 */
@XmlRootElement
public class FileSystemItems {

	private final List<FileSystemItem> directories = newArrayList();

	private final List<FileSystemItem> scripts = newArrayList();

	/**
	 * @return the directories
	 */
	@XmlElement(name = "directory")
	public List<FileSystemItem> getDirectories() {
		return directories;
	}

	/**
	 * @return the scripts
	 */
	@XmlElement(name = "script")
	public List<FileSystemItem> getScripts() {
		return scripts;
	}
}

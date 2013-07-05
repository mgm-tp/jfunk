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

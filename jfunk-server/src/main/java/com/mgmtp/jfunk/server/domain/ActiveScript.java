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

import java.net.URI;
import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * Pojo representing an active script. An active script can either be scheduled (
 * {@link ActiveState#SCHEDULED}) or running ({@link ActiveState#RUNNING}) depending on the
 * availability of free worker threads in the thread pool executing scripts.
 * 
 * @author rnaegele
 */
@XmlRootElement(name = "script")
public class ActiveScript extends FileSystemItem {

	// changes are made by different threads, so we need volatile for visibility
	private volatile ActiveState state;
	private volatile UUID id;

	/**
	 * Enum specifying whether a script is scheduled or already running.
	 */
	public enum ActiveState {
		/** State for a scheduled script waiting for a worker thread to be available. */
		SCHEDULED,
		/** State for a script that is currently being executed. */
		RUNNING
	}

	public ActiveScript() {
		super();
	}

	/**
	 * @param id
	 *            a unique ID for the script
	 * @param name
	 *            the name of the script
	 * @param lastModified
	 *            the last modification date of the script
	 * @param uri
	 *            the URI which the script can be reached under
	 */
	public ActiveScript(final UUID id, final String name, final Date lastModified, final URI uri, final ActiveState state) {
		super(name, lastModified, uri);
		this.id = id;
		this.state = state;
	}

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final UUID id) {
		this.id = id;
	}

	/**
	 * @return the state
	 */
	public ActiveState getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(final ActiveState state) {
		this.state = state;
	}
}
/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.server.domain;

import java.net.URI;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Pojo representing a file system item, which can be a Grovvy script or a directory.
 * 
 * @author rnaegele
 * @version $Id$
 */
@XmlRootElement
public class FileSystemItem {

	private String name;
	private Date lastModified;
	private URI uri;

	public FileSystemItem() {
		this(null, null, null);
	}

	/**
	 * @param name
	 *            the name of the script
	 * @param lastModified
	 *            the last modification date of the script
	 * @param uri
	 *            the URI which the script can be reached under
	 */
	public FileSystemItem(final String name, final Date lastModified, final URI uri) {
		this.name = name;
		this.lastModified = lastModified;
		this.uri = uri;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the lastModified
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified
	 *            the lastModified to set
	 */
	public void setLastModified(final Date lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @return the uri
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * @param uri
	 *            the uri to set
	 */
	public void setUri(final URI uri) {
		this.uri = uri;
	}
}

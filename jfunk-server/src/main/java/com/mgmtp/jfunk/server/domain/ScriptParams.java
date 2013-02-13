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
 * Container class for script parameters.
 * 
 * @author rnaegele
 * @version $Id$
 */
@XmlRootElement
public class ScriptParams {

	@XmlElement(name = "scriptParam")
	private final List<ScriptParam> scriptParams = newArrayList();

	/**
	 * @return the scriptParams
	 */
	public List<ScriptParam> getScriptParams() {
		return scriptParams;
	}
}

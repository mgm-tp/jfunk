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
package com.mgmtp.jfunk.core.event;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;

/**
 * @author rnaegele
 */
public abstract class CommandEvent extends AbstractBaseEvent {

	private final String command;
	private final List<Object> params;

	public CommandEvent(final String command, final Object[] params) {
		this.command = command;
		this.params = Collections.unmodifiableList(asList(params));
	}

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @return the params
	 */
	public List<Object> getParams() {
		return params;
	}
}

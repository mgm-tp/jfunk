/*
 * Copyright (c) 2015 mgm technology partners GmbH
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

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

/**
 * Event handler for logging all {@code before} and {@code after} events at debug level.
 * 
 * @author rnaegele
 */
@Singleton
public class EventLoggingEventHandler {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Subscribe
	@AllowConcurrentEvents
	public void handleEvent(final Object event) {
		String name = event.getClass().getSimpleName();
		if (name.startsWith("Before")) {
			log.debug(">> {}", event);
		} else if (name.startsWith("After")) {
			log.debug("<< {}", event);
		} else {
			// do nothing
		}
	}
}

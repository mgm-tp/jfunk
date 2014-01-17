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
package com.mgmtp.jfunk.core.exception;

import com.mgmtp.jfunk.core.step.base.Step;

/**
 * This exception is thrown when the execution of a {@link Step} produces an error.
 * 
 */
public class StepException extends RuntimeException {
	public StepException(final String message) {
		super(message);
	}

	public StepException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public static StepException wrapThrowable(final String message, final Throwable th) {
		Throwable localTh = th;
		while (localTh instanceof StepException) {
			Throwable cause = localTh.getCause();
			if (cause == null) {
				break;
			}
			localTh = cause;
		}
		return new StepException(message + ". Original error: " + localTh.getMessage(), localTh);
	}
}
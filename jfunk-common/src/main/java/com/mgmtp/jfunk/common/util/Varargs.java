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
package com.mgmtp.jfunk.common.util;

/**
 * Utility class for transforming var args into object arrays.
 * 
 * @author rnaegele
 */
public final class Varargs {

	private Varargs() {
		// don't allow instantiation
	}

	/**
	 * Creates an object array from the specified var args arguments.
	 * 
	 * @param args
	 *            the arguments
	 * @return the object array
	 */
	public static <T> T[] va(final T... args) {
		return args;
	}
}
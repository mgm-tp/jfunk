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
package com.mgmtp.jfunk.common.util;

/**
 * @deprecated no longer needed, relates to deprecated step modes
 */
@Deprecated
public final class DataUtils {

	private DataUtils() {
		// don't allow instantiation
	}

	/**
	 * Checks if a given value is the default value. Default value can be
	 * <ul>
	 * <li>{@code null}</li>
	 * <li>empty string</li>
	 * <li>0</li>
	 * <li>0,00</li>
	 * </ul>
	 * 
	 * @param value
	 *            the value to check
	 * @return {@code true} if the value is the default value, {@code false} otherwise
	 */
	public static boolean isDefaultValue(final String value) {
		if (value == null || value.equals("") || value.equals("0,00") || value.equals("0")) {
			return true;
		}
		return false;
	}
}
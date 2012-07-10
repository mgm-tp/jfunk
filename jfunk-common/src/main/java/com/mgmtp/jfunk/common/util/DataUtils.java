package com.mgmtp.jfunk.common.util;

/**
 * @version $Id$
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
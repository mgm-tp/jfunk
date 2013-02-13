/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.common.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * <p>
 * Properties implementation that does not have the encoding restrictions of
 * {@link java.util.Properties}.
 * </p>
 * Note that all operations consider potential defaults, e. g. the results returned by methods such
 * as {@link #size()}, {@link #keySet}, or {@link #entrySet} always consider potential defaults.
 * This in turn, however, means that removal operations also affect defaults (e. g. {@link #clear()}
 * ).
 *
 * @version $Id$
 */
@NotThreadSafe
public class ExtendedProperties implements Map<String, String>, Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	private static final ThreadLocal<List<String>> TOKEN = new ThreadLocal<List<String>>() {
		@Override
		protected List<String> initialValue() {
			return Lists.newArrayListWithCapacity(256);
		}
	};

	private static final Logger LOG = Logger.getLogger(ExtendedProperties.class);

	private Map<String, String> propsMap;
	private Map<String, String> defaults;

	private transient Set<String> keySet;
	private transient Set<Entry<String, String>> entrySet;
	private transient Collection<String> values;

	/**
	 * Creates an empty instance.
	 */
	public ExtendedProperties() {
		propsMap = new MapMaker().makeMap();
	}

	/**
	 * Creates an empty instance with the specified defaults.
	 *
	 * @param defaults
	 *            The defaults
	 */
	public ExtendedProperties(final Map<String, String> defaults) {
		this();
		this.defaults = defaults;
	}

	/**
	 * Created an instance from the specified {@link java.util.Properties} instance.
	 *
	 * @param props
	 *            The properties instance
	 * @return The {@link ExtendedProperties} instance
	 */
	public static ExtendedProperties fromProperties(final Properties props) {
		ExtendedProperties result = new ExtendedProperties();
		for (Enumeration<?> en = props.propertyNames(); en.hasMoreElements();) {
			String key = (String) en.nextElement();
			String value = props.getProperty(key);
			result.put(key, value);
		}
		return result;
	}

	/**
	 * Removes all properties and potential defaults.
	 */
	@Override
	public void clear() {
		propsMap.clear();
		defaults = null;
	}

	/**
	 * Looks up a property. Recursively checks the defaults if necessary. Internally,
	 * {@code get(key, true)} is called to look up the value.
	 *
	 * @param key
	 *            The property key
	 * @return The property value, or {@code null} if the property is not found
	 */
	@Override
	public String get(final Object key) {
		return get(key, true);
	}

	/**
	 * Looks up a property. Recursively checks the defaults if necessary. Internally,
	 * {@code get(key, true)} is called to look up the value.
	 *
	 * @param key
	 *            The property key
	 * @param defaultValue
	 *            The value returned if the property is not found
	 * @return The property value, or the specified default value if the property is not found
	 */
	public String get(final Object key, final String defaultValue) {
		String val = get(key, true);
		return val == null ? defaultValue : val;
	}

	/**
	 * Looks up a property. Recursively checks the defaults if necessary. If the property is found
	 * as a system property, this value will be used.
	 *
	 * @param key
	 *            The property key
	 * @param process
	 *            If {@code true}, the looked-up value is passed to
	 *            {@link #processPropertyValue(String)}, and the processed result is returned.
	 * @return The property value, or {@code null} if the property is not found
	 */
	public String get(final Object key, final boolean process) {
		// Check system properties first
		String value = System.getProperty(String.valueOf(key));
		if (value == null) {
			value = propsMap.get(key);
		}

		if (process) {
			value = processPropertyValue(value);
		}
		return value == null && defaults != null ? defaults.get(key) : value;
	}

	public boolean getBoolean(final String key) {
		return Boolean.parseBoolean(get(key, ""));
	}

	public boolean getBoolean(final String key, final boolean defaultValue) {
		String value = get(key);
		return StringUtils.isNotEmpty(value) ? Boolean.parseBoolean(value) : defaultValue;
	}

	public long getLong(final String key) {
		String value = get(key);
		return Long.parseLong(value);
	}

	public long getLong(final String key, final long defaultValue) {
		String value = get(key);
		return StringUtils.isNotEmpty(value) ? Long.parseLong(value) : defaultValue;
	}

	public int getInteger(final String key) {
		String value = get(key);
		return Integer.parseInt(value);
	}

	public int getInteger(final String key, final int defaultValue) {
		String value = get(key);
		return StringUtils.isNotEmpty(value) ? Integer.parseInt(value) : defaultValue;
	}

	/**
	 * Sets the property with the specified key.
	 *
	 * @param key
	 *            The key (may not be {@code null})
	 * @param value
	 *            The value (may not be {@code null})
	 * @return The previous value of the property, or {@code null} if it did not have one
	 */
	@Override
	public String put(final String key, final String value) {
		return propsMap.put(key, value);
	}

	/**
	 * Return {@code true} if the property with the specified key exists. Defaults are considered in
	 * the result.
	 *
	 * @param key
	 *            The key
	 * @return {@code true} if the property with the specified key exists
	 */
	@Override
	public boolean containsKey(final Object key) {
		return System.getProperty(key.toString()) != null ? true : propsMap.containsKey(key) ? true : defaults != null && defaults.containsKey(key);
	}

	/**
	 * Return {@code true} if a property with the specified value exists. Defaults are considered in
	 * the result.
	 *
	 * @param value
	 *            The value
	 * @return {@code true} if a property with the specified value exists
	 */
	@Override
	public boolean containsValue(final Object value) {
		for (String key : keySet()) {
			if (value.equals(get(key))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a set of Entries representing property key-value pairs. The returned set is a view to
	 * the internal data structures and reflects changes to this instance. Potential defaults are
	 * included in the returned set.
	 *
	 * @return The entry set
	 */
	@Override
	public Set<Entry<String, String>> entrySet() {
		if (entrySet == null) {
			entrySet = new EntrySet();
		}
		return entrySet;
	}

	/**
	 * Returns {@code true} if no properties are available. Defaults are considered in the result.
	 *
	 * @return {@code true} if no properties are available
	 */
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Adds all key-value pairs from the specified map.
	 *
	 * @param map
	 *            The map
	 */
	@Override
	public void putAll(final Map<? extends String, ? extends String> map) {
		for (Entry<? extends String, ? extends String> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Removes the property with the specified key. Defaults are considered.
	 *
	 * @param key
	 *            The key
	 * @return The previous value associated with the property
	 */
	@Override
	public String remove(final Object key) {
		String result = propsMap.remove(key);
		if (defaults != null) {
			String s = defaults.remove(key);
			if (result == null) {
				result = s;
			}
		}
		return result;
	}

	/**
	 * Returns a collection of property values. The returned collection is a view to the internal
	 * data structures and reflects changes to this instance. Potential defaults are included in the
	 * returned collection.
	 *
	 * @return The values collection
	 */
	@Override
	public Collection<String> values() {
		if (values == null) {
			values = new Values();
		}
		return values;
	}

	/**
	 * Returns a set of property keys. The returned set is a view to the internal data structures
	 * and reflects changes to this instance. Potential defaults are included in the returned set.
	 *
	 * @return The key set
	 */
	@Override
	public Set<String> keySet() {
		if (keySet == null) {
			keySet = new KeySet();
		}
		return keySet;
	}

	/**
	 * Calculates and returns the number of properties. Defaults are considered in the result.
	 *
	 * @return The number of properties
	 */
	@Override
	public int size() {
		return keySet().size();
	}

	/**
	 * <p>
	 * Replaces tokens like <code>${xxx}</code> contain in {@code input} with the value of the
	 * property with the key {@code xxx}, or the system property with that key. In case of no match,
	 * the tokens remain unchanged.
	 * </p>
	 * <p>
	 * A default value may be specified separated by a comma from the property key:
	 * <code>${xxx,yyy}</code>. The default value may be the empty string.
	 * </p>
	 * <p>
	 * Example:<br />
	 * If neither a property nor a system property with the key {@code archive.dir} is found,
	 * <code>c:/temp/${archive.dir,dummy_test}/testruns</code>, the default value "dummy_test" is
	 * resolved for <code>${archive.dir,dummy_test}<code> and the result is {@code
	 * c:/temp/dummy_test/testruns}.
	 * </p>
	 * <p>
	 * Tokens may be nested, e. g. <code>${key${user.dir,c:/${current.dir}/archive},default}}</code>
	 * .
	 * </p>
	 *
	 * @param input
	 *            The input string
	 * @return The processed value
	 */
	public String processPropertyValue(final String input) {
		if (input == null) {
			return null;
		}
		// Check if there is anything to do at all...
		int startIndex = input.indexOf("${");
		if (startIndex == -1) {
			return input;
		}
		if (input.indexOf('}', startIndex) == -1) {
			return input;
		}
		String prefix;
		String property;
		String postfix;
		// as the first tag has been searched the beginning is tagfree
		prefix = input.substring(0, startIndex);
		// Search for a matching brace for the Open tag
		int openTags = 1;
		int endIndex = startIndex;
		while (endIndex < input.length() - 1 && openTags > 0) {
			endIndex++;
			if (input.charAt(endIndex) == '}') {
				openTags--;
			} else if (input.charAt(endIndex) == '$' && input.charAt(endIndex + 1) == '{') {
				openTags++;
			}
		}
		// if the openTag is not matching a closing tag we will return it as is
		if (openTags > 0) {
			return prefix + "${" + processPropertyValue(input.substring(startIndex + 2));
		}
		// register token to catch infinite loops
		List<String> tokens = TOKEN.get();
		if (tokens.contains(input)) {
			StringBuilder buffer = new StringBuilder(256);
			for (String s : tokens) {
				buffer.append(s);
				buffer.append("-->");
			}
			throw new IllegalStateException("The string '" + input + "' is already being processed; this results in a short circuit: " + buffer);
		}
		tokens.add(input);
		// now the startIndex is at the nun  ${ and endIndex at the associated }, so process the internal
		// first process that inside of the tags
		String inner = processPropertyValue(input.substring(startIndex + 2, endIndex));
		int index = inner.indexOf(',');
		String key;
		if (index == -1) {
			// kein default replacement
			key = inner;
		} else {
			// default replacement
			key = inner.substring(0, index);
		}
		property = null;
		if (key.length() == 0) {
			LOG.warn("'" + input + "' contains an empty placeholder token ${...} which is not allowed");
		} else {
			property = get(key);
			if (property == null) {
				property = System.getProperty(key);
			}
		}
		// no value found; default value
		if (property == null) {
			if (index == -1) {
				// kein default inner, so return everything
				property = "${" + key + "}";
			} else {
				property = inner.substring(index + 1);
			}
		}
		// the string part after the tag still has to be processed
		postfix = processPropertyValue(input.substring(endIndex + 1));
		// remove token as this here is done
		tokens.remove(input);
		// assemble and return value
		String result = prefix + property + postfix;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Processing property " + input + " to " + result);
		}
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ExtendedProperties other = (ExtendedProperties) obj;
		return entrySet().equals(other.entrySet());
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		for (Entry<String, String> entry : entrySet()) {
			hashCode += entry.hashCode();
		}
		return hashCode;
	}

	/**
	 * Returns a new {@link java.util.Properties} instance representing the properties.
	 *
	 * @return The properties
	 */
	public Properties toProperties() {
		Properties props = new Properties();
		// We need to iterate over the keys calling
		// getProperty in order to consider defaults.
		for (String key : keySet()) {
			props.put(key, get(key));
		}
		return props;
	}

	/**
	 * Returns a string representation of this instance. Returns all properties recursively
	 * including defaults.
	 *
	 * @return The string representation of the instance
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(propsMap);
		if (defaults != null) {
			sb.append("\nDefaults: ");
			sb.append(defaults);
		}
		return sb.toString();
	}

	/**
	 * Loads properties from the specified stream using the default encoding. The caller of this
	 * method is responsible for closing the stream.
	 *
	 * @param is
	 *            The input stream
	 */
	public void load(final InputStream is) throws IOException {
		load(new InputStreamReader(is));
	}

	/**
	 * Loads properties from the specified stream. The caller of this method is responsible for
	 * closing the stream.
	 *
	 * @param is
	 *            The input stream
	 * @param encoding
	 *            The encoding
	 */
	public void load(final InputStream is, final String encoding) throws IOException {
		load(new InputStreamReader(is, encoding));
	}

	/**
	 * Loads properties from the specified reader. The caller of this method is responsible for
	 * closing the reader.
	 *
	 * @param reader
	 *            The reader
	 */
	public void load(final Reader reader) throws IOException {
		doLoad(new LineReader(reader));
	}

	private void doLoad(final LineReader lr) throws IOException {
		char[] convtBuf = new char[1024];
		int limit;
		int keyLen;
		int valueStart;
		char c;
		boolean hasSep;
		boolean precedingBackslash;

		while ((limit = lr.readLine()) >= 0) {
			c = 0;
			keyLen = 0;
			valueStart = limit;
			hasSep = false;

			precedingBackslash = false;
			while (keyLen < limit) {
				c = lr.lineBuf[keyLen];
				//need check if escaped.
				if ((c == '=' || c == ':') && !precedingBackslash) {
					valueStart = keyLen + 1;
					hasSep = true;
					break;
				} else if ((c == ' ' || c == '\t' || c == '\f') && !precedingBackslash) {
					valueStart = keyLen + 1;
					break;
				}
				if (c == '\\') {
					precedingBackslash = !precedingBackslash;
				} else {
					precedingBackslash = false;
				}
				keyLen++;
			}
			while (valueStart < limit) {
				c = lr.lineBuf[valueStart];
				if (c != ' ' && c != '\t' && c != '\f') {
					if (!hasSep && (c == '=' || c == ':')) {
						hasSep = true;
					} else {
						break;
					}
				}
				valueStart++;
			}
			String key = loadConvert(lr.lineBuf, 0, keyLen, convtBuf);
			String value = loadConvert(lr.lineBuf, valueStart, limit - valueStart, convtBuf);
			put(key, value);
		}
	}

	/**
	 * Writes the properties to the specified stream using the default encoding, including defaults.
	 *
	 * @param os
	 *            The output stream
	 * @param comments
	 *            Header comment that is written to the stream
	 * @param sorted
	 *            If {@code true}, the properties are written sorted by key
	 * @param process
	 *            If {@code true}, place holders are resolved
	 */
	public void store(final OutputStream os, final String comments, final boolean sorted, final boolean process) throws IOException {
		store(new OutputStreamWriter(os), comments, sorted, process);
	}

	/**
	 * Writes the properties to the specified stream, including defaults.
	 *
	 * @param os
	 *            The output stream
	 * @param encoding
	 *            The encoding
	 * @param comments
	 *            The header comment written to the stream
	 * @param sorted
	 *            If {@code true}, the properties are written sorted by key
	 * @param process
	 *            If {@code true}, place holders are resolved
	 */
	public void store(final OutputStream os, final String encoding, final String comments, final boolean sorted, final boolean process)
			throws IOException {
		store(new OutputStreamWriter(os, encoding), comments, sorted, process);
	}

	/**
	 * Writes the properties to the specified writer, including defaults.
	 *
	 * @param writer
	 *            The writer
	 * @param comments
	 *            The header comment written to the writer
	 * @param sorted
	 *            If {@code true}, the properties are written sorted by key
	 * @param process
	 *            If {@code true}, place holders are resolved
	 */
	public void store(final Writer writer, final String comments, final boolean sorted, final boolean process) throws IOException {
		BufferedWriter bw = writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer);

		if (comments != null) {
			for (Scanner scanner = new Scanner(comments); scanner.hasNextLine();) {
				bw.write("#");
				bw.write(scanner.nextLine());
				bw.newLine();
			}
		}

		bw.write("#" + new Date());
		bw.newLine();
		Set<String> keys = keySet();
		if (sorted) {
			keys = Sets.newTreeSet(keys);
		}

		for (String key : keys) {
			/*
			 * No need to escape embedded and trailing spaces for value, hence pass false to flag.
			 */
			bw.write(saveConvert(key, true) + "=" + saveConvert(get(key, process), false));
			bw.newLine();
		}
		bw.flush();
	}

	@Override
	public ExtendedProperties clone() {
		try {
			ExtendedProperties clone = (ExtendedProperties) super.clone();
			clone.keySet = null;
			clone.entrySet = null;
			clone.values = null;
			clone.propsMap = new ConcurrentHashMap<String, String>(propsMap);
			if (defaults != null) {
				// This recursively clones defaults.
				if (defaults instanceof ExtendedProperties) {
					clone.defaults = ((ExtendedProperties) defaults).clone();
				} else {
					clone.defaults = Maps.newHashMap(defaults);
				}
			}
			return clone;
		} catch (CloneNotSupportedException ex) {
			// can't happen
			throw new IllegalStateException(ex);
		}
	}

	/*
	 * Read in a "logical line" from an InputStream/Reader, skip all comment and blank lines and
	 * filter out those leading whitespace characters ( , and ) from the beginning of a
	 * "natural line". Method returns the char length of the "logical line" and stores the line in
	 * "lineBuf".
	 */
	// --> Copied and adapted from the JDK
	static class LineReader {
		public LineReader(final Reader reader) {
			this.reader = reader;
			inCharBuf = new char[8192];
		}

		char[] inCharBuf;
		char[] lineBuf = new char[1024];
		int inLimit = 0;
		int inOff = 0;
		Reader reader;

		int readLine() throws IOException {
			int len = 0;
			char c = 0;

			boolean skipWhiteSpace = true;
			boolean isCommentLine = false;
			boolean isNewLine = true;
			boolean appendedLineBegin = false;
			boolean precedingBackslash = false;
			boolean skipLF = false;

			while (true) {
				if (inOff >= inLimit) {
					inLimit = reader.read(inCharBuf);
					inOff = 0;
					if (inLimit <= 0) {
						if (len == 0 || isCommentLine) {
							return -1;
						}
						return len;
					}
				}

				c = inCharBuf[inOff++];
				if (skipLF) {
					skipLF = false;
					if (c == '\n') {
						continue;
					}
				}
				if (skipWhiteSpace) {
					if (c == ' ' || c == '\t' || c == '\f') {
						continue;
					}
					if (!appendedLineBegin && (c == '\r' || c == '\n')) {
						continue;
					}
					skipWhiteSpace = false;
					appendedLineBegin = false;
				}
				if (isNewLine) {
					isNewLine = false;
					if (c == '#' || c == '!') {
						isCommentLine = true;
						continue;
					}
				}

				if (c != '\n' && c != '\r') {
					lineBuf[len++] = c;
					if (len == lineBuf.length) {
						int newLength = lineBuf.length * 2;
						if (newLength < 0) {
							newLength = Integer.MAX_VALUE;
						}
						char[] buf = new char[newLength];
						System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
						lineBuf = buf;
					}
					//flip the preceding backslash flag
					if (c == '\\') {
						precedingBackslash = !precedingBackslash;
					} else {
						precedingBackslash = false;
					}
				} else {
					// reached EOL
					if (isCommentLine || len == 0) {
						isCommentLine = false;
						isNewLine = true;
						skipWhiteSpace = true;
						len = 0;
						continue;
					}
					if (inOff >= inLimit) {
						inLimit = reader.read(inCharBuf);
						inOff = 0;
						if (inLimit <= 0) {
							return len;
						}
					}
					if (precedingBackslash) {
						len -= 1;
						//skip the leading whitespace characters in following line
						skipWhiteSpace = true;
						appendedLineBegin = true;
						precedingBackslash = false;
						if (c == '\r') {
							skipLF = true;
						}
					} else {
						return len;
					}
				}
			}
		}
	}

	/*
	 * Converts encoded &#92;uxxxx to unicode chars and changes special saved chars to their
	 * original forms
	 */
	//--> Copied and adapted from the JDK
	private String loadConvert(final char[] in, int off, final int len, char[] convtBuf) {
		if (convtBuf.length < len) {
			int newLen = len * 2;
			if (newLen < 0) {
				newLen = Integer.MAX_VALUE;
			}
			convtBuf = new char[newLen];
		}
		char aChar;
		char[] out = convtBuf;
		int outLen = 0;
		int end = off + len;

		while (off < end) {
			aChar = in[off++];
			if (aChar == '\\') {
				aChar = in[off++];
				switch (aChar) {
					case 't':
						aChar = '\t';
						break;
					case 'r':
						aChar = '\r';
						break;
					case 'n':
						aChar = '\n';
						break;
					case 'f':
						aChar = '\f';
						break;
				}
				out[outLen++] = aChar;
			} else {
				out[outLen++] = aChar;
			}
		}
		return new String(out, 0, outLen);
	}

	/*
	 * Escapes special characters with a preceding slash
	 */
	//--> Copied and adapted from the JDK
	private String saveConvert(final String theString, final boolean escapeSpace) {
		int len = theString.length();
		int bufLen = len * 2;
		if (bufLen < 0) {
			bufLen = Integer.MAX_VALUE;
		}
		StringBuilder outBuffer = new StringBuilder(bufLen);

		for (int x = 0; x < len; x++) {
			char aChar = theString.charAt(x);
			// Handle common case first, selecting largest block that
			// avoids the specials below
			if (aChar > 61 && aChar < 127) {
				if (aChar == '\\') {
					outBuffer.append('\\');
					outBuffer.append('\\');
					continue;
				}
				outBuffer.append(aChar);
				continue;
			}
			switch (aChar) {
				case ' ':
					if (x == 0 || escapeSpace) {
						outBuffer.append('\\');
					}
					outBuffer.append(' ');
					break;
				case '\t':
					outBuffer.append('\\');
					outBuffer.append('t');
					break;
				case '\n':
					outBuffer.append('\\');
					outBuffer.append('n');
					break;
				case '\r':
					outBuffer.append('\\');
					outBuffer.append('r');
					break;
				case '\f':
					outBuffer.append('\\');
					outBuffer.append('f');
					break;
				case '=': // Fall through
				case ':': // Fall through
				case '#': // Fall through
				case '!':
					outBuffer.append('\\');
					outBuffer.append(aChar);
					break;
				default:
					outBuffer.append(aChar);
			}
		}
		return outBuffer.toString();
	}

	final class KeySet extends AbstractSet<String> {

		@Override
		public Iterator<String> iterator() {
			return new PropertiesIterator<String>(propsMap.keySet(), defaults != null ? defaults.keySet() : null, false);
		}

		@Override
		public int size() {
			// We cannot used EnhancedProperties.this.size() because it relies on this method.
			Set<String> result = Sets.newHashSet(propsMap.keySet());
			if (defaults != null) {
				// Recursion using keySet()!
				result.addAll(defaults.keySet());
			}
			return result.size();
		}

		@Override
		public boolean contains(final Object o) {
			return ExtendedProperties.this.containsKey(o);
		}

		@Override
		public boolean remove(final Object o) {
			return ExtendedProperties.this.remove(o) != null;
		}

		@Override
		public void clear() {
			ExtendedProperties.this.clear();
		}

		@Override
		public Object[] toArray() {
			Collection<String> c = toCollection();
			return c.toArray();
		}

		@Override
		public <T> T[] toArray(final T[] a) {
			Collection<String> c = toCollection();
			return c.toArray(a);
		}

		private Collection<String> toCollection() {
			Collection<String> c = Lists.newArrayList();
			for (String string : this) {
				c.add(string);
			}
			return c;
		}
	}

	final class Values extends AbstractCollection<String> {
		@Override
		public Iterator<String> iterator() {
			return new PropertiesIterator<String>(propsMap.values(), defaults != null ? defaults.values() : null, true);
		}

		@Override
		public int size() {
			return ExtendedProperties.this.size();
		}

		@Override
		public boolean contains(final Object o) {
			return ExtendedProperties.this.containsValue(o);
		}

		@Override
		public void clear() {
			ExtendedProperties.this.clear();
		}

		@Override
		public Object[] toArray() {
			Collection<String> c = toCollection();
			return c.toArray();
		}

		@Override
		public <T> T[] toArray(final T[] a) {
			Collection<String> c = toCollection();
			return c.toArray(a);
		}

		private Collection<String> toCollection() {
			Collection<String> c = Lists.newArrayList();
			for (String string : this) {
				c.add(string);
			}
			return c;
		}
	}

	final class EntrySet extends AbstractSet<Map.Entry<String, String>> {
		@Override
		public Iterator<Map.Entry<String, String>> iterator() {
			Set<Map.Entry<String, String>> defaultsEntrySet = defaults != null ? defaults.entrySet() : null;
			return new PropertiesIterator<Map.Entry<String, String>>(propsMap.entrySet(), defaultsEntrySet, false);
		}

		@Override
		public boolean contains(final Object o) {
			if (!(o instanceof Map.Entry<?, ?>)) {
				return false;
			}
			Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
			String value = ExtendedProperties.this.get(e.getKey());
			return value != null && value.equals(e.getValue());
		}

		@Override
		public boolean remove(final Object o) {
			if (!(o instanceof Map.Entry<?, ?>)) {
				return false;
			}
			Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
			return ExtendedProperties.this.remove(e.getKey()) != null;
		}

		@Override
		public int size() {
			return ExtendedProperties.this.size();
		}

		@Override
		public void clear() {
			ExtendedProperties.this.clear();
		}

		@Override
		public Object[] toArray() {
			Collection<Map.Entry<String, String>> c = toCollection();
			return c.toArray();
		}

		@Override
		public <T> T[] toArray(final T[] a) {
			Collection<Map.Entry<String, String>> c = toCollection();
			return c.toArray(a);
		}

		private Collection<Map.Entry<String, String>> toCollection() {
			Collection<Map.Entry<String, String>> c = Lists.newArrayListWithExpectedSize(size());
			for (Map.Entry<String, String> entry : this) {
				c.add(new SimpleEntry(entry));
			}
			return c;
		}
	}

	/**
	 * Simple implementation, copied and slightly adapted from java.util.AbstractMap.SimpleEntry.
	 */
	static final class SimpleEntry implements Entry<String, String> {
		String key;
		String value;

		public SimpleEntry(final String key, final String value) {
			this.key = key;
			this.value = value;
		}

		public SimpleEntry(final Entry<String, String> e) {
			this.key = e.getKey();
			this.value = e.getValue();
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public String getValue() {
			return value;
		}

		@Override
		public String setValue(final String value) {
			String oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		@Override
		public boolean equals(final Object o) {
			if (!(o instanceof Map.Entry<?, ?>)) {
				return false;
			}
			Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
			return eq(key, e.getKey()) && eq(value, e.getValue());
		}

		@Override
		public int hashCode() {
			return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
		}

		@Override
		public String toString() {
			return key + "=" + value;
		}

		boolean eq(final Object o1, final Object o2) {
			return o1 == null ? o2 == null : o1.equals(o2);
		}
	}

	/**
	 * {@link Iterator} implementation considering a default collection.
	 */
	static final class PropertiesIterator<T> implements Iterator<T> {

		private final Iterator<T> iter;
		private final Iterator<T> defaultsIter;
		private boolean defaultsMode;
		private final boolean readonlyIterator;

		PropertiesIterator(final Collection<T> coll, final Collection<T> defaultsColl, final boolean readonlyIterator) {
			iter = coll.iterator();
			if (defaultsColl != null) {
				defaultsIter = defaultsColl.iterator();
			} else {
				defaultsIter = null;
			}
			this.readonlyIterator = readonlyIterator;
		}

		@Override
		public boolean hasNext() {
			if (iter.hasNext()) {
				return true;
			}
			if (defaultsIter != null) {
				defaultsMode = true;
				return defaultsIter.hasNext();
			}
			return false;
		}

		@Override
		public T next() {
			if (iter.hasNext()) {
				return iter.next();
			}
			if (defaultsIter != null) {
				defaultsMode = true;
				return defaultsIter.next();
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			if (readonlyIterator) {
				throw new UnsupportedOperationException("Iterator is read-only.");
			}
			if (defaultsMode) {
				defaultsIter.remove();
			} else {
				iter.remove();
			}
		}
	}
}
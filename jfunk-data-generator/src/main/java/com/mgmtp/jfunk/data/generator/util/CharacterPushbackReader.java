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
package com.mgmtp.jfunk.data.generator.util;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

/**
 * Helper class to read and also write back a string character by character. This helper class only
 * extends the basic functionality by implementing the ready method more precise. Until all
 * characters are read the stream is ready and once all characters have been read the method ready
 * will return false.
 * 
 */
public class CharacterPushbackReader extends PushbackReader {
	private final int length;
	private int used;

	public CharacterPushbackReader(final String expression) {
		super(new StringReader(expression));
		length = expression.length();
	}

	/**
	 * Reads the next character from the string
	 */
	public char readChar() throws IOException {
		used++;
		return (char) read();
	}

	/**
	 * Puts the character from the string back
	 */
	public void unread(final char c) throws IOException {
		used--;
		super.unread(c);
	}

	/**
	 * Returns true as long as used &lt; length && super.ready()
	 * 
	 * @see PushbackReader#ready()
	 */
	@Override
	public boolean ready() throws IOException {
		return used < length && super.ready();
	}
}
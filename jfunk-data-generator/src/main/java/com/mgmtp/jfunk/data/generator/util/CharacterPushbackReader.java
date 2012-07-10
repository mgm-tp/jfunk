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
 * @version $Id$
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
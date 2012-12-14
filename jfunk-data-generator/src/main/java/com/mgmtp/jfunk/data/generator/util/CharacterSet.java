/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdom.Element;


/**
 * This class represents all characters supported by a given encoding. It can be limited further
 * using a regular expression, so only characters matching the expression will be retained.
 * 
 * @version $Id$
 */
public class CharacterSet {

	public static final Logger LOG = Logger.getLogger(CharacterSet.class);

	private static ThreadLocal<Map<String, CharacterSet>> characterSets = new ThreadLocal<Map<String, CharacterSet>>() {
		@Override
		protected Map<String, CharacterSet> initialValue() {
			return new HashMap<String, CharacterSet>();
		}
	};

	private String characterSetId;
	private final Map<Character, Boolean> map = new HashMap<Character, Boolean>();
	private char[] characters;
	private char[] forbiddenCharacters;

	/**
	 * Used only internally to initialize an empty instance.
	 * 
	 * @see #getInverse()
	 */
	private CharacterSet() {
		// Used only internally to initialize an empty instance.
	}

	/**
	 * Creates a new instance. All characters matching the given regular expression will be added to
	 * the character array. If the expression is null all characters supported by this encoding will
	 * be used. The encoding used must only use one Byte per character as simply all 0-255 char
	 * values are used to determine the associated character. Should the encoding use more than 1
	 * Byte an IllegalArgumentException is thrown.
	 */
	CharacterSet(final String encoding, final String goodExpression, final String badExpression, final String characterSetId)
			throws UnsupportedEncodingException {
		this.characterSetId = characterSetId;
		Charset charset = Charset.forName(encoding);
		CharsetEncoder encoder = charset.newEncoder();
		int bytesPerChar = (int) (encoder.maxBytesPerChar() + 0.5);
		if (bytesPerChar > 1) {
			throw new IllegalArgumentException("this characterset class can currently handle only one byte charsets!");
		}
		characters = new char[256];
		for (int i = 0; i < characters.length; i++) {
			String s = new String(new byte[] { (byte) i }, encoding);
			characters[i] = s.charAt(0);
		}
		forbiddenCharacters = new char[0];
		if (goodExpression != null && goodExpression.length() > 0 && badExpression != null && badExpression.length() > 0) {
			ArrayList<Character> list = new ArrayList<Character>(characters.length);
			ArrayList<Character> exList = new ArrayList<Character>(characters.length);
			Pattern goodPattern = Pattern.compile(goodExpression);
			Pattern badPattern = Pattern.compile(badExpression);
			for (char character : characters) {
				Matcher goodM = goodPattern.matcher(Character.toString(character));
				Matcher badM = badPattern.matcher(Character.toString(character));
				if (goodM.matches()) {
					map.put(character, Boolean.TRUE);
					list.add(character);
				} else {
					map.put(character, Boolean.FALSE);
				}
				if (badM.matches()) {
					exList.add(character);
				}
			}
			characters = new char[list.size()];
			for (int i = 0; i < characters.length; i++) {
				characters[i] = list.get(i);
			}
			forbiddenCharacters = new char[exList.size()];
			for (int i = 0; i < forbiddenCharacters.length; i++) {
				forbiddenCharacters[i] = exList.get(i);
			}
		}
	}

	public static void initCharacterSet(final Element characterSetElement) throws UnsupportedEncodingException {
		String characterSetId = characterSetElement.getAttributeValue(XMLTags.ID);
		String encoding = characterSetElement.getChildText(XMLTags.ENCODING);
		String goodExpression = characterSetElement.getChildText(XMLTags.GOOD_EXPRESSION);
		String badExpression = characterSetElement.getChildText(XMLTags.BAD_EXPRESSION);

		CharacterSet cs = new CharacterSet(encoding, goodExpression, badExpression, characterSetId);
		characterSets.get().put(characterSetId, cs);
		LOG.info("Added " + cs);
	}

	/**
	 * Returns a CharacterSet instance whose allowed and forbidden characters are exactly inverse to
	 * this instance.
	 * 
	 * @return a CharacterSet instance whose allowed and forbidden characters are exactly inverse to
	 *         this instance
	 */
	public CharacterSet getInverse() {
		CharacterSet n = new CharacterSet();
		n.forbiddenCharacters = characters;
		n.characters = forbiddenCharacters;
		return n;
	}

	/**
	 * Returns the number of characters this CharacterSet contains.
	 * 
	 * @return the number of characters this CharacterSet contains
	 */
	public int getCharacterCount() {
		return characters.length;
	}

	public int getForbiddenCharacterCount() {
		return forbiddenCharacters.length;
	}

	/**
	 * Returns the forbidden character at index position
	 * 
	 * @return the forbidden character at index position
	 */
	public char getForbiddenChar(final int index) {
		return forbiddenCharacters[index];
	}

	/**
	 * Return the character at index position
	 * 
	 * @return the character at index position
	 */
	public char getCharacter(final int index) {
		if (index > characters.length) {
			throw new IndexOutOfBoundsException(index + "@" + characters.length + " does not fit");
		}
		return characters[index];
	}

	public Set<Character> getCharacters() {
		return Collections.unmodifiableSet(map.keySet());
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(characters.length * 4 + 33);

		buf.append("CharacterSet [id=");
		buf.append(characterSetId);
		buf.append(", \n\tgood: (");
		for (int i = 0; i < characters.length; i++) {
			if (i > 0) {
				buf.append(',');
			}
			buf.append('\'');
			buf.append(characters[i]);
			buf.append('\'');
		}
		buf.append(")\n\tbad: (");
		for (int i = 0; i < forbiddenCharacters.length; i++) {
			if (i > 0) {
				buf.append(',');
			}
			buf.append('\'');
			buf.append(forbiddenCharacters[i]);
			buf.append('\'');
		}
		buf.append(")\n]");

		return buf.toString();
	}

	public boolean isAllowed(final char ch) {
		Boolean allowed = map.get(ch);
		if (allowed == null) {
			throw new IllegalArgumentException("The character" + ch + " is not part of the CharacterSet.");
		}
		return allowed;
	}

	/**
	 * The method searches for a CharacterSet element in the document root and returns a
	 * correspondingly initialized CharacterSet Object.
	 * 
	 * @return a CharacterSet initialized from the element
	 */
	public static CharacterSet getCharacterSet(final String characterSetId) {
		return characterSets.get().get(characterSetId);
	}

	/**
	 * Creates a new CharacterSet. If there is already a CharacterSet with the given ID this will be
	 * returned.
	 */
	public static CharacterSet createCharacterSet(final String encoding, final String goodExpression, final String badExpression,
			final String characterSetId) throws UnsupportedEncodingException {
		if (characterSets.get().containsKey(characterSetId)) {
			LOG.info("CharacterSet with id=" + characterSetId + " already created");
			return characterSets.get().get(characterSetId);
		}
		CharacterSet cs = new CharacterSet(encoding, goodExpression, badExpression, characterSetId);
		characterSets.get().put(characterSetId, cs);
		LOG.info("Added " + cs);
		return cs;
	}
}
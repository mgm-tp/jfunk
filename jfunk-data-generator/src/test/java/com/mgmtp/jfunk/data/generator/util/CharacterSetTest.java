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
package com.mgmtp.jfunk.data.generator.util;

import static org.testng.Assert.assertFalse;

import java.io.UnsupportedEncodingException;

import org.testng.annotations.Test;

import com.mgmtp.jfunk.data.generator.util.CharacterSet;

/**
 * Unit test for {@link CharacterSet}
 * 
 */
public class CharacterSetTest {

	private static final String GOOD_EXP_LATIN_1 = "[^\\x00-\\x1F\\x7F\\x80-\\x9F¤¦¨´¸¼½¾­]";
	private static final String BAD_EXP_LATIN_1 = "[[^\\x00-\\x1F\\x7F\\x80-\\x9F\\s­][ ]]";

	private static final String GOOD_EXP_LATIN_9 = "[^\\x00-\\x1F\\x7F\\x80-\\x9FŠšŽžŒœŸ­]";
	private static final String BAD_EXP_LATIN_9 = "[[^\\x00-\\x1F\\x7F\\x80-\\x9F\\s­][ ]]";

	private static final char[] BAD_CHARS = new char[] {
			// Latin-1
			'¤', // A4
			'¦', // A6
			'¨', // A8
			'´', // B4
			'¸', // B8
			'¼', // BC
			'½', // BD
			'¾', // BE
			// Latin-9
			'Š', // A6
			'š', // A8
			'Ž', // B4
			'ž', // B8
			'Œ', // BC
			'œ', // BD
			'Ÿ', // BE
			// Latin-1 und Latin-9
			'­' // AD
	};

	@Test
	public void testLatin1() throws UnsupportedEncodingException {
		CharacterSet charSet = new CharacterSet("ISO-8859-1", GOOD_EXP_LATIN_1, BAD_EXP_LATIN_1, "Latin-1");
		assertBadChars(charSet);
	}

	@Test
	public void testLatin9() throws UnsupportedEncodingException {
		CharacterSet charSet = new CharacterSet("ISO-8859-15", GOOD_EXP_LATIN_9, BAD_EXP_LATIN_9, "Latin-9");
		assertBadChars(charSet);
	}

	private void assertBadChars(final CharacterSet charSet) {
		for (char ch : BAD_CHARS) {
			assertFalse(charSet.getCharacters().contains(ch) && charSet.isAllowed(ch));
		}
	}
}
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
package com.mgmtp.jfunk.data.generator.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;

/**
 * Part of a GeneratingExpression Structure. During parsing the underlying regular expression is
 * divided into individual nodes. This class represents one node. Every node contains a character
 * class and an allowed range of length that indicates how many characters of the class can be
 * generated.
 * 
 */
public class Node {

	public final Logger log = Logger.getLogger(getClass());

	private final Range range;
	private final CharacterField field;
	private final MathRandom rnd;

	public Node(final String expression, final Range r, final CharacterSet set, final MathRandom random) {
		range = r;
		field = new CharacterField(random, expression, set);
		rnd = random;
	}

	public boolean canBad() {
		return field.canBad();
	}

	/**
	 * Returns the allowed range of length, how many characters of the character set are allowed in
	 * a string.
	 * 
	 * @return the range of this node
	 */
	public Range getRange() {
		return range;
	}

	/**
	 * Returns size characters in an array. If bad is set to true at least half of all the
	 * characters will be set to a forbidden character.
	 * 
	 * @param bad
	 *            0 equals no bad character, 1 equals 1 - all are bad and 2 equals all are bad
	 * @return a character string
	 */
	public char[] getCharacters(final int size, final int bad) {
		if (size == 0) {
			return new char[0];
		}
		char[] chars = new char[size];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = field.getAllowedCharacter();
		}
		// remove leading blanks
		for (int i = 0; i < 10 && Character.isSpaceChar(chars[0]); i++) {
			chars[0] = field.getAllowedCharacter();
		}
		// remove trailing blanks
		for (int i = 0; i < 10 && Character.isSpaceChar(chars[chars.length - 1]); i++) {
			chars[chars.length - 1] = field.getAllowedCharacter();
		}
		int toBeReplaced = bad;
		if (-2 == bad) {
			if (!canBad()) {
				if (log.isDebugEnabled()) {
					log.debug("This node does not allow negative characters, " + "but as all characters shall be negative no character is returned");
				}
				return new char[0];
			}
			toBeReplaced = chars.length;
		} else if (-1 == bad) {
			// 1 - (all -1) characters are replaced
			toBeReplaced = 1 + rnd.getInt(chars.length - 2);
		}
		if (toBeReplaced > 0) {
			// create a random index list
			List<Integer> l = new ArrayList<Integer>(chars.length);
			for (int i = 0; i < chars.length; i++) {
				l.add(i);
			}
			Collections.shuffle(l);
			for (int i = 0; i < toBeReplaced; i++) {
				int index = l.remove(0);
				Character currentBad = field.getForbiddenCharacter();
				if (currentBad == null) {
					log.warn("This node does not allow negative characters");
					break;
				}
				// The first and the last character as a forbidden character is not a space!
				// this would usually shorten the length
				if (index == 0 || index == chars.length - 1) {
					int counter = 0;
					while (counter < 100 && Character.isSpaceChar(currentBad)) {
						currentBad = field.getForbiddenCharacter();
						counter++;
					}
					if (counter == 100) {
						log.warn("Space is the only possible forbidden character for this node, no replacement!");
						break;
					}
				}
				if (log.isDebugEnabled()) {
					log.debug("Replace Character " + Character.toString(chars[index]) + " with " + Character.toString(currentBad));
				}
				chars[index] = currentBad;
			}
		}
		return chars;
	}
}
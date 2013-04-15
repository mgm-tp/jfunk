/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.random.RandomCollection;
import com.mgmtp.jfunk.common.random.Randomizable;

/**
 * Helper class dividing a given CharacterSet into allowed and forbidden characters using a one
 * character regular expression. All characters matching the regular expression are allowed, all
 * others are forbidden. The method getAllowedCharacter then provides an allowed, the method
 * getForbiddenCharacter a forbidden character.
 * 
 */
class CharacterField {

	protected Logger log = Logger.getLogger(getClass());
	private final Randomizable<Character> allowed;
	private final Randomizable<Character> forbidden;
	private final boolean canBad;

	/**
	 * Initializes the new instance using the regular expression. Every character of the
	 * CharacterSet passed which matches the regular expression is part of the allowed characters,
	 * every other is part of the forbidden characters.
	 */
	public CharacterField(final MathRandom random, final String expression, final CharacterSet set) {
		Pattern pattern = Pattern.compile(expression);
		// First: collect all allowed characters in the CharacterSet
		Collection<Character> forbiddenChars = new ArrayList<Character>(256);
		for (int i = 0; i < set.getForbiddenCharacterCount(); i++) {
			Character character = set.getForbiddenChar(i);
			if (set.isAllowed(character)) {
				// Characters that are also within the allowed range are only excluded from the local expression
				Matcher m = pattern.matcher(character.toString());
				if (!m.matches()) {
					forbiddenChars.add(character);
				}
			} else {
				// If the character is  marked as forbidden globally, it is also a forbidden character for this field.
				forbiddenChars.add(character);
			}
		}
		Collection<Character> allowedChars = new ArrayList<Character>(256);
		for (int i = 0; i < set.getCharacterCount(); i++) {
			Character character = set.getCharacter(i);
			Matcher m = pattern.matcher(character.toString());
			if (m.matches()) {
				allowedChars.add(character);
			}
		}
		if (allowedChars.isEmpty()) {
			log.warn("No character allowed for expression " + expression);
		}
		canBad = !forbiddenChars.isEmpty();
		allowed = new RandomCollection<Character>(random, allowedChars);
		forbidden = new RandomCollection<Character>(random, forbiddenChars);
	}

	/**
	 * Provides a random allowed character
	 * 
	 * @return a random allowed character or null if none is allowed
	 */
	public Character getAllowedCharacter() {
		return allowed.get();
	}

	/**
	 * Provides a random forbidden character
	 * 
	 * @return a random forbidden character or null if all are allowed
	 */
	public Character getForbiddenCharacter() {
		return forbidden.get();
	}

	/**
	 * Provides true if there is at least one forbidden character
	 * 
	 * @return true if there is at least one forbidden character
	 */
	public boolean canBad() {
		return canBad;
	}
}
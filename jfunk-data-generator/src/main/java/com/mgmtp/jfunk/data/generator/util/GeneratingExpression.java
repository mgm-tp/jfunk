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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;

import com.mgmtp.jfunk.common.random.Choice;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;

/**
 * This class is some kind of inverse regular expression. It is inititalized with a CharacterSet and
 * a regular expression. This expression is then used to determine which characters can be generated
 * in which numbers. Thereby the regular expression is limited to character classes and set
 * operators. An expression with the form [A-Z]{3,5}-?[0-9]{3,} generates character string with the
 * form ABC-123, DEFZL123456789 etc.
 * 
 */
public class GeneratingExpression {

	private static final Pattern UNESCAPED_PIPE_PATTERN = Pattern.compile("(?<!\\\\)\\|");

	private final Logger log = Logger.getLogger(getClass());

	private final StringBuffer buf = new StringBuffer();
	private final List<Node> nodes;
	private Range range;
	private final Choice choice;
	private final MathRandom random;

	/**
	 * Generates a new instance.
	 * 
	 * @param expression
	 *            the regular expression determines the allowed character strings
	 * @param set
	 *            the CharacterSet that determines the basic set of characters
	 */
	public GeneratingExpression(final MathRandom random, final String expression, final CharacterSet set) throws IOException {
		this.random = random;
		String exp = chooseAlternations(expression);
		CharacterPushbackReader in = new CharacterPushbackReader(exp);
		nodes = new ArrayList<Node>();
		choice = new Choice(random);
		while (in.ready()) {
			String characterExpression = parseNextCharacterExpression(in);
			Range r = parseNextNumberExpression(in);
			Node node = new Node(characterExpression, r, set, random);
			if (range == null) {
				range = r;
			} else {
				range = range.sumBoundaries(r);
			}
			nodes.add(node);
		}
	}

	/**
	 * Resolves alternations by randomly choosing one at a time and adapting the pattern accordingly
	 * 
	 * @param expression
	 *            the pattern
	 * @return the reduced pattern
	 */
	private String chooseAlternations(final String expression) {
		StrBuilder sb = new StrBuilder(expression);

		int i = 0;
		// Loop until an unescaped pipe symbol appears.
		while (UNESCAPED_PIPE_PATTERN.matcher(sb.toString()).find()) {
			for (; i < sb.length(); ++i) {
				if (sb.charAt(i) == '|') {
					if (sb.charAt(i - 1) == '\\') {
						// escapet
						continue;
					}

					int start = i;
					// Backtrack until an opening bracket is found
					// to limit the context of alternatives.
					for (int closingCount = 0; start >= 0; --start) {
						char c = sb.charAt(start);
						if (c == '(') {
							if (closingCount == 0) {
								break;
							}
							--closingCount;
						} else if (c == ')') {
							++closingCount;
						}
					}

					if (start >= 0) {
						// If an opening brace was found
						// search for a closing bracket.
						int end = i;
						for (int openingCount = 0; end < sb.length(); ++end) {
							char c = sb.charAt(end);
							if (c == '(') {
								++openingCount;
							} else if (c == ')') {
								if (openingCount == 0) {
									break;
								}
								--openingCount;
							}
						}
						String alternative = random.getBoolean() ? sb.substring(start + 1, i) : sb.substring(i + 1, end);
						sb.replace(start, end + 1, alternative);
						i = start + alternative.length();
						break;
					}
					String alternative = random.getBoolean() ? sb.substring(0, i) : sb.substring(i + 1);
					sb.replace(0, sb.length() + 1, alternative);
					break;
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Replaces randomly selected characters in the given string with forbidden characters according
	 * to the given expression.
	 * 
	 * @param bad
	 *            if bad = -2 all characters are replaced, if bad = -1 1 - (all - 1) are replaced,
	 *            if bad > 0 the exact number of characters is replaced
	 * @param input
	 *            the input string
	 * @return input with <code>bad</code> replaced characters
	 */
	public String negateString(final String input, int bad) {
		int length = input.length();
		Range[] ranges = getRanges();
		int[] lengths = new int[ranges.length];
		// arrange lengths
		for (int i = 0; i < lengths.length; i++) {
			Range r = ranges[i];
			lengths[i] = r.getMin();
			length -= r.getMin();
		}
		/**
		 * distribute remaining lengths
		 */
		int i = 0;
		// only 1000 tries otherwise break as it just does not work
		while (length > 0 && i < 1000) {
			int index = i % lengths.length;
			Range r = ranges[index];
			if (lengths[index] < r.getMax()) {
				lengths[index] += 1;
				length--;
			}
			i++;
		}

		// generate completely negative string
		String replaceString = generate(lengths, -2);
		if (replaceString.length() == 0) {
			log.warn("No negative characters possible in this expression. All characters are allowed.");
			return input;
		}
		// now replace the #bad character in the input string
		List<Integer> l = new ArrayList<Integer>(input.length());
		for (i = 0; i < input.length(); i++) {
			l.add(i);
		}
		if (bad == -2) {
			// all false
			bad = input.length();
		} else if (bad == -1) {
			bad = 1 + random.getInt(input.length() - 1);
		}
		Collections.shuffle(l);
		StringBuffer base = new StringBuffer(input);
		int j = 0;
		for (i = 0; i < bad; i++) {
			int index = l.remove(0);
			char replaceChar = ' ';
			if (index < replaceString.length()) {
				replaceChar = replaceString.charAt(index);
			}
			while ((index == 0 || index >= replaceString.length() || index == input.length() - 1) && Character.isSpaceChar(replaceChar)) {
				replaceChar = replaceString.charAt(j);
				j = (j + 1) % replaceString.length();
			}
			base.setCharAt(index, replaceChar);
		}
		if (log.isDebugEnabled()) {
			log.debug("False characters in string; " + input + " became " + base);
		}
		return base.toString();
	}

	/**
	 * The range for this GeneratingExpression. The Range is between the minimum necessary and the
	 * maximum allows characters
	 * 
	 * @return the range of this GeneratingExpression
	 */
	public Range getRange() {
		return range;
	}

	/**
	 * As the regular expression was distributed in separate node, every node has its own range.
	 * This method returns an array containing all range objects.
	 * 
	 * @return the separate range objects for all sub-nodes of this expression
	 */
	public Range[] getRanges() {
		Range[] ranges = new Range[nodes.size()];
		for (int i = 0; i < ranges.length; i++) {
			ranges[i] = nodes.get(i).getRange();
		}
		return ranges;
	}

	/**
	 * Generates a string containing subject to the parameter value only allowed or one half of
	 * forbidden characters.
	 * 
	 * @param nodeSizes
	 *            Array with the desire string lengths. For every node there must be only one length
	 * @param bad
	 *            if 0, only good characters are generated, if 1, 1 - (all - 1) characters are
	 *            generated false, if 2 all characters are generated false.
	 * @return a string
	 */
	public String generate(final int[] nodeSizes, final int bad) {
		buf.setLength(0);
		for (int i = 0; i < nodes.size() && i < nodeSizes.length; i++) {
			buf.append(nodes.get(i).getCharacters(nodeSizes[i], bad));
		}
		String value = buf.toString();
		buf.setLength(0);
		return value;
	}

	/**
	 * Returns a string whose length is always exactly in the middle of the allowed range
	 * 
	 * @param bad
	 *            if 0, only allowed characters are generated, if 1, 1 - (all - 1) characters are
	 *            generated false, if 2 all characters are generated false.
	 * @return a string
	 */
	public String generate(final int bad) {
		int[] sizes = new int[nodes.size()];
		for (int i = 0; i < sizes.length; i++) {
			Range r = nodes.get(i).getRange();
			sizes[i] = r.getMin() + r.getRange() / 2;
		}
		return generate(sizes, bad);
	}

	/**
	 * Generates a string with the given length. Thereby the mandatory length will be reached first
	 * and then the separate areas are filled with the remaining characters randlomly.
	 */
	public String generate(int total, final int bad) {
		if (total < 0) {
			if (log.isDebugEnabled()) {
				log.debug("Character string cannot have a negative length!");
			}
			total = 0;
		}
		Range[] ranges = getRanges();
		int[] lengths = new int[ranges.length];
		int length = 0;
		// first make sure every single node has at least its minimum length
		for (int i = 0; i < ranges.length; i++) {
			lengths[i] = ranges[i].getMin();
			length += lengths[i];
		}
		// now increase each node chosen randomly by one until the extra is consumed
		int index = 0;
		boolean increase = length < total;
		if (increase) {
			boolean maxedOut = total > getRange().getMax();
			while (length < total) {
				// if maxed out is true, we have more than normal max characters, so
				// we ignore the local max; if not maxed out, the nodes are not bigger than max
				if ((maxedOut || ranges[index].getMax() > lengths[index]) && choice.get()) {
					lengths[index]++;
					length++;
				}
				index = (index + 1) % lengths.length;
			}
		} else {
			boolean minOut = total < getRange().getMin();
			while (length > total) {
				// if min out is true, we have less than normal min characters, so
				// we ignore the local min; if not min out, the nodes are not smaller than min
				if ((minOut || ranges[index].getMin() > lengths[index]) && lengths[index] > 0 && choice.get()) {
					lengths[index]--;
					length--;
				}
				index = (index + 1) % lengths.length;
			}
		}
		// now we have distributed the character number to all subnodes of this expression
		// build buffer
		return generate(lengths, bad);
	}

	private String parseNextCharacterExpression(final CharacterPushbackReader in) throws IOException {
		StringBuffer sb = new StringBuffer();
		char c = in.readChar();
		sb.append(c);
		if ('[' == c) {
			int openBraces = 1;
			while (in.ready() && openBraces > 0) {
				c = in.readChar();
				sb.append(c);
				if ('[' == c) {
					openBraces++;
				} else if (']' == c) {
					openBraces--;
				}
			}
			if (openBraces > 0) {
				throw new IllegalArgumentException("the given regular expression was not valid; missing ]");
			}
		} else if ('\\' == c) {
			c = in.readChar();
			sb.append(c);
			if ('p' == c) {
				// read \p{...} class definition
				c = in.readChar();
				sb.append(c);
				if ('{' == c) {
					while (in.ready() && c != '}') {
						c = in.readChar();
						sb.append(c);
					}
					if (c != '}') {
						throw new IllegalArgumentException("the given regular expression was not valid; missing }");
					}
				}
			}
		}
		return sb.toString();
	}

	private Range parseNextNumberExpression(final CharacterPushbackReader in) throws IOException {
		char c = in.readChar();
		int[] ar = new int[2];
		ar[0] = -1;
		ar[1] = -1;
		if ('{' == c) {
			StringBuffer sb = new StringBuffer();
			c = in.readChar();
			// after { now so min number is coming
			while (c != '}' && in.ready()) {
				if (',' == c) {
					try {
						ar[0] = Integer.parseInt(sb.toString());
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("if giving number of occurrences with {n,m} you must specify a value for n", e);
					}
					sb.setLength(0);
				} else {
					sb.append(c);
				}
				c = in.readChar();
			}
			if (sb.length() > 0) {
				ar[1] = Integer.parseInt(sb.toString());
			}
			if (ar[0] == -1) {
				ar[0] = ar[1];
			}
		} else if ('?' == c) {
			ar[0] = 0;
			ar[1] = 1;
		} else if ('+' == c) {
			ar[0] = 1;
		} else if ('*' == c) {
			ar[0] = 0;
		} else {
			ar[0] = 1;
			ar[1] = 1;
			in.unread(c);
		}
		return new Range(ar[0], ar[1]);
	}
}
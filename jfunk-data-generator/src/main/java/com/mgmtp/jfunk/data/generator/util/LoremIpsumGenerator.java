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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.text.StrBuilder;

import com.mgmtp.jfunk.data.generator.config.LoremIpsum;

/**
 * @author rnaegele
 */
@Singleton
public class LoremIpsumGenerator {

	private final String loremIpsum;
	private final int loremLength;

	@Inject
	public LoremIpsumGenerator(@LoremIpsum final String loremIpsum) {
		this.loremIpsum = loremIpsum;
		loremLength = loremIpsum.length();
	}

	public String generateLoremIpsum(final int length) {
		if (loremLength < length) {
			int multiplier = length / loremLength;
			int remainder = length % loremLength;

			StrBuilder sb = new StrBuilder(multiplier * length + remainder);
			for (int i = 0; i < multiplier; ++i) {
				sb.append(loremIpsum);
			}
			sb.append(loremIpsum.substring(0, remainder));
			return sb.toString();
		}

		return loremIpsum.substring(0, length);
	}
}

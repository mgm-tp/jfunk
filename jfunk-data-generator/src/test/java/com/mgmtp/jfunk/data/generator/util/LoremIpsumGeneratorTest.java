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

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * @author rnaegele
 */
public class LoremIpsumGeneratorTest {

	private LoremIpsumGenerator generator;
	private String loremIpsum;

	@BeforeClass
	public void setUpClass() throws IOException {
		loremIpsum = Resources.toString(Resources.getResource("lorem_ipsum.txt"), Charsets.UTF_8);
		generator = new LoremIpsumGenerator(loremIpsum);
	}

	@Test
	public void testMinimum() {
		String result = generator.generateLoremIpsum(1);
		assertEquals(result, "L");
	}

	@Test
	public void testMaximum() {
		String result = generator.generateLoremIpsum(loremIpsum.length());
		assertEquals(result, loremIpsum);
	}

	@Test
	public void testMultiple() {
		String result = generator.generateLoremIpsum(10000);
		assertEquals(result.length(), 10000);
	}
}

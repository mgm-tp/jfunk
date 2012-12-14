/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
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
 * @version $Id: $
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

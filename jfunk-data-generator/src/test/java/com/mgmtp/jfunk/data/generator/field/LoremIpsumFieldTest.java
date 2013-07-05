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
package com.mgmtp.jfunk.data.generator.field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jdom.Element;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * 
 * @author rnaegele
 */
public class LoremIpsumFieldTest {

	private String loremIpsum;

	private LoremIpsumField field;

	@BeforeClass
	public void setUpClass() throws IOException {
		loremIpsum = Resources.toString(Resources.getResource("lorem_ipsum.txt"), Charsets.UTF_8);
	}

	@BeforeMethod
	public void setUpTest() {
		Element element = mock(Element.class);
		when(element.getChildText(XMLTags.MIN)).thenReturn("1");
		when(element.getChildText(XMLTags.MAX)).thenReturn("100");
		field = new LoremIpsumField(new MathRandom(), element, null);
		field.loremIpsum = loremIpsum;
	}

	@Test
	public void testMinimum() {
		String value = field.getString(new FieldCase(1));
		assertEquals(value, "L");
	}

	@Test
	public void testMaximum() {
		String value = field.getString(new FieldCase(100));
		assertEquals(value, loremIpsum.substring(0, 100));
	}

	@Test
	public void testVeryLongOne() {
		String value = field.getString(new FieldCase(10000));
		assertEquals(value.length(), 10000);
	}
}

/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.benerator;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.DataSet;

/**
 * Unit test for {@link BeneratorDataSource}.
 * 
 * @version $Id$
 */
public class BeneratorDataSourceTest {

	private BeneratorDataSource bs;
	private List<Element> attributes;

	/**
	 * This method is annotated with {@link BeforeMethod}, because we need a fresh data source for
	 * every test.
	 */
	@BeforeMethod
	public void setUp() throws IOException, JDOMException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		InputStream is = null;
		try {
			Configuration config = new Configuration(Charsets.UTF_8);
			is = cl.getResourceAsStream("test-datasources.properties");
			config.load(is);
			bs = new BeneratorDataSource(config);
		} finally {
			IOUtils.closeQuietly(is);
		}

		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			is = cl.getResourceAsStream("benerator/benerator.test.xml");
			Document doc = saxBuilder.build(is);
			Element root = doc.getRootElement();
			attributes = root.getChild("create-entities", root.getNamespace()).getChildren("attribute", root.getNamespace());
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	@Test
	public void testNextDataSet() {
		DataSet data = bs.getNextDataSet("ustva");

		for (Element elem : attributes) {
			Assert.assertTrue(data.hasValue(elem.getAttributeValue("name")));
		}

		data = bs.getNextDataSet("test");
		Assert.assertTrue(data.hasValue("foo"));
		Assert.assertTrue(data.hasValue("jahr"));
	}
}

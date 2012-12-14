/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.samples.unit;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.mgmtp.jfunk.unit.JFunkJUnitSupport;
import com.mgmtp.jfunk.unit.JFunkRunner;

/**
 * @author rnaegele
 * @version $Id$
 */
@RunWith(JFunkJUnitSupport.class)
public class JUnitGoogleTest {

	@Inject
	JFunkRunner jFunkRunner;

	@Test
	public void testGoogle() {
		jFunkRunner.generate("google");
		jFunkRunner.set("archive.dir", "testruns/junit");
		String searchTerm = jFunkRunner.get("${google searchTerm}");
		jFunkRunner.run(new UnitTestModule(searchTerm));
	}
}

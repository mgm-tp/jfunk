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
package com.mgmtp.jfunk.samples.unit;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.mgmtp.jfunk.samples.google.GoogleAdvancedSearchModule;
import com.mgmtp.jfunk.unit.JFunkJUnitSupport;
import com.mgmtp.jfunk.unit.JFunkRunner;

/**
 * @author rnaegele
 */
@RunWith(JFunkJUnitSupport.class)
public class JUnitGoogleTest {

	@Inject
	JFunkRunner jFunkRunner;

	@Test
	public void testGoogle() {
		jFunkRunner.set("archive.dir", "testruns/junit");

		jFunkRunner.prepareNextDataSet("google");
		jFunkRunner.run(new GoogleAdvancedSearchModule());
	}
}

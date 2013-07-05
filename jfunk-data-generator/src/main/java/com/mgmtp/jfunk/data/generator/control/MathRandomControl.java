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
package com.mgmtp.jfunk.data.generator.control;

import java.util.List;

import org.jdom.Element;

import com.google.common.collect.Lists;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * Returns a configurable number of cases within the allowed range. Every case is a random value
 * within the allowed range ({@link FieldCase#AVG}).
 * 
 */
public class MathRandomControl extends BaseFieldControl {

	private final int caseCount;

	public MathRandomControl(final MathRandom random, final Element el, final Range r) {
		super(random, el, r);
		caseCount = Integer.parseInt(el.getChildText(XMLTags.CASES));
	}

	@Override
	protected List<FieldCase> createCases() {
		List<FieldCase> list = Lists.newArrayListWithExpectedSize(caseCount);
		while (list.size() < caseCount) {
			list.add(createCase(FieldCase.AVG));
		}
		return list;
	}

	@Override
	public int countCases() {
		return caseCount;
	}
}
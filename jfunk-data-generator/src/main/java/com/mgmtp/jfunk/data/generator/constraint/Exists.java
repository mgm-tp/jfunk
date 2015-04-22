/*
 * Copyright (c) 2015 mgm technology partners GmbH
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
package com.mgmtp.jfunk.data.generator.constraint;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Element;

import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.base.SourceConstraint;
import com.mgmtp.jfunk.data.generator.control.FieldCase;

/**
 * This constraint gets a sub-constraint. If the sub-constraint returns a value unequal null, the
 * constraint returns the value {@code true}, if not the value {@code false}. Can be used with a
 * mapping constraint.
 * <p>
 * Example:
 * 
 * <pre>
 * {@code
 * <constraint class="com.mgmtp.jfunk.data.generator.constraint.Exists">
 *   <constraint_ref id="constraint_id"/>
 * </constraint>
 * }
 * </pre>
 * 
 */
public class Exists extends SourceConstraint {

	public Exists(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
	}

	@Override
	protected String initValuesImpl(final FieldCase c) throws Exception {
		String value = source.initValues(c);
		if (StringUtils.isNotEmpty(value)) {
			return JFunkConstants.TRUE;
		}
		return JFunkConstants.FALSE;
	}

	/**
	 * Always returns 1.
	 */
	@Override
	public int countCases() {
		return 1;
	}

	/**
	 * Always returns false.
	 */
	@Override
	public boolean hasNextCase() {
		return false;
	}
}
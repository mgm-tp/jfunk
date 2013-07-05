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
package com.mgmtp.jfunk.data.generator.constraint;

import java.util.UUID;

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.base.BaseStateConstraint;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.control.FieldControl;

/**
 * A constraint that represents an immutable universally unique identifier (UUID). A UUID represents
 * a 128-bit value. Uses {@link UUID}.
 * 
 */
public class UUIDConstraint extends BaseStateConstraint {

	private final FieldControl fc;

	public UUIDConstraint(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
		fc = generator.getControlFactory().getControl(random, element, new Range(1, 3));
	}

	@Override
	protected String initValuesImpl(FieldCase c) throws Exception {
		c = fc.getNext(c);
		if (c.isBad()) {
			// TODO implement bad case
			return UUID.randomUUID().toString();
		}
		return UUID.randomUUID().toString();
	}

	@Override
	protected void resetValuesImpl() {
		// nothing to do here
	}

	@Override
	public int countCases() {
		return fc.countCases();
	}

	@Override
	public int getMaxLength() {
		return 36;
	}

	@Override
	public boolean hasNextCase() {
		return fc.hasNext();
	}

	@Override
	public void resetCase() {
		fc.reset();
	}
}
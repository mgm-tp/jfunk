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

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.base.SourceConstraint;
import com.mgmtp.jfunk.data.generator.control.FieldCase;

/**
 * This constraint executes a reset not directly but delays it until initValues() is called the next
 * time. Usually the reset is executed directly and this lead to loss of the values. For example
 * within a mapping constrain all referenced entry constraints are reset at reset. To avoid this if
 * it shall for example be achieved that only the selected constraint is reset and not all, this
 * constraint offers an option. The reset is stored and will be effectively executed with a delay.
 * 
 */
public class OnDemandReset extends SourceConstraint {

	private boolean pendingReset;

	public OnDemandReset(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
		pendingReset = false;
	}

	@Override
	protected String initValuesImpl(final FieldCase c) throws Exception {
		if (pendingReset) {
			pendingReset = false;
			source.resetValues();
		}
		return super.initValuesImpl(c);
	}

	@Override
	public void resetValues() {
		pendingReset = true;
	}
}

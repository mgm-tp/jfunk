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

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.base.SourceConstraint;

/**
 * This constraint passes the value of another constraint. This one will be initialized but not
 * reset. This allows the value of a field to be used at multiple instances. The single calls of the
 * method are simply passed to the source constraint with the exception of the reset methods - those
 * have to be called directly on the source constraint.
 * 
 */
public class Forward extends SourceConstraint {

	public Forward(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
	}
}
/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
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
 * @version $Id$
 */
public class Forward extends SourceConstraint {

	public Forward(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
	}
}
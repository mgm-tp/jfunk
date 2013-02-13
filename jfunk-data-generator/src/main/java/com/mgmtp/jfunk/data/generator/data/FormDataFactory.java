/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.data;

import java.util.Map;

import com.mgmtp.jfunk.data.generator.Generator;

/**
 * FormData factory interface. The implementation is created dynamically by Guice.
 * 
 * @version $Id$
 */
public interface FormDataFactory {
	FormData create(final String key, final Generator generator, final Map<String, Map<String, String>> fixedValuesMap);
}

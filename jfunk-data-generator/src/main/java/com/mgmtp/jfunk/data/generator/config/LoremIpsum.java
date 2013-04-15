/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import com.mgmtp.jfunk.common.JFunkConstants;

/**
 * Qualifier annotation for injecting the archive base directory as set in the configuration by
 * {@link JFunkConstants#ARCHIVE_DIR}.
 * 
 * @author rnaegele
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Qualifier
public @interface LoremIpsum {
	//
}
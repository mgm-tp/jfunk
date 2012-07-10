package com.mgmtp.jfunk.core.scripting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import com.mgmtp.jfunk.common.util.Disposable;

/**
 * Qualifier annotation for the module-scoped set of {@link Disposable}s.
 * 
 * @author rnaegele
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Qualifier
public @interface ModuleScopedDisposables {
	//
}
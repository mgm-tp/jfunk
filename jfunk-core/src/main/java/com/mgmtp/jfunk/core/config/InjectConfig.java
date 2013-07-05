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
package com.mgmtp.jfunk.core.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Annotation for injecting configuration values.
 * </p>
 * <p>
 * Usage:
 * 
 * <pre>
 * &#064;InjectConfig(name = &quot;myConfigString&quot;, defaultValue = &quot;foo&quot;)
 * String myConfigItem;
 * 
 * &#064;InjectConfig(name = &quot;myConfigBoolean&quot;)
 * boolean myConfigBoolean;
 * </pre>
 * 
 * </p>
 * 
 * @author rnaegele
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InjectConfig {
	static final String NO_DEFAULT = "<NO_DEFAULT_VALUE_SPECIFIED>";

	String name();

	String defaultValue() default NO_DEFAULT;
}
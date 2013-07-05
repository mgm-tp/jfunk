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
package com.mgmtp.jfunk.core.reporting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mgmtp.jfunk.core.module.TestModule;

/**
 * {@link TestModule}s are reported automatically but can be excluded from reports by annotating the
 * with {@code @Reported(false)}. Steps may be included in reports by annotating the with this
 * annotation.
 * 
 * @author rnaegele
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Reported {

	/**
	 * Specifies whether or not the object is to be included in a report.
	 * 
	 * @return {@code true} if the object is to be included in a report, {@code false} otherwise
	 */
	boolean value() default true;
}

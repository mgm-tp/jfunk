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
package com.mgmtp.jfunk.data.generator.data;

import java.util.Map;

import com.mgmtp.jfunk.data.generator.Generator;

/**
 * FormData factory interface. The implementation is created dynamically by Guice.
 * 
 */
public interface FormDataFactory {
	FormData create(final String key, final Generator generator, final Map<String, Map<String, String>> fixedValuesMap);
}

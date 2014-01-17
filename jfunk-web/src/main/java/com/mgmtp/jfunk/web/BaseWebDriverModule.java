/*
 * Copyright (c) 2014 mgm technology partners GmbH
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
package com.mgmtp.jfunk.web;

import javax.inject.Provider;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebWindowListener;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.mgmtp.jfunk.core.config.BaseJFunkGuiceModule;

/**
 * @author rnaegele
 */
public abstract class BaseWebDriverModule extends BaseJFunkGuiceModule {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	private Multibinder<WebDriverEventListener> webDriverEventListenersBinder;
	private Multibinder<WebWindowListener> webWindowListenersBinder;

	@Override
	protected final void doConfigure() {
		webDriverEventListenersBinder = Multibinder.newSetBinder(binder(), WebDriverEventListener.class);
		webWindowListenersBinder = Multibinder.newSetBinder(binder(), WebWindowListener.class);

		doConfigureWebDriverModule();
	}

	protected abstract void doConfigureWebDriverModule();

	protected void bindWebDriver(final String webDriverKey, final Class<? extends Provider<? extends WebDriver>> providerClass) {
		bind(WebDriver.class).annotatedWith(Names.named(webDriverKey)).toProvider(providerClass);
	}

	protected LinkedBindingBuilder<WebDriverEventListener> bindWebDriverEventListener() {
		return webDriverEventListenersBinder.addBinding();
	}

	protected LinkedBindingBuilder<WebWindowListener> bindWebWindowListener() {
		return webWindowListenersBinder.addBinding();
	}
}

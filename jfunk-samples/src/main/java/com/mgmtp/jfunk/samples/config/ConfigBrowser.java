/*
 * Copyright (c) 2016 mgm technology partners GmbH
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
package com.mgmtp.jfunk.samples.config;

import java.io.File;

import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.ie.InternetExplorerDriverService;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Singleton;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.core.config.InjectConfig;
import com.mgmtp.jfunk.web.event.BeforeWebDriverCreationEvent;

@Singleton
public class ConfigBrowser {

    @InjectConfig(name = "webdriver.chrome.driver")
    private String relativePathToChromeDriver;

    @InjectConfig(name = "webdriver.ie.driver")
    private String relativePathToIeDriver;

    @InjectConfig(name = "browser")
    private String browser;

    @Subscribe
    public void configureBrowser(final BeforeWebDriverCreationEvent event) {

        switch (browser) {
            case "firefox": {
                // configure Firefox
            }
            case "chrome": {
                setPathToDriverIfExistsAndIsExecutable(relativePathToChromeDriver, ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY);
                // configure Chrome
                break;
            }
            case "ie": {
                setPathToDriverIfExistsAndIsExecutable(relativePathToIeDriver, InternetExplorerDriverService.IE_DRIVER_EXE_PROPERTY);
                // configure InternetExplorer
                break;
            }
            default: {
                throw new RuntimeException(String.format("Please configure one of the supported browsers in %s", JFunkConstants.SCRIPT_PROPERTIES));
            }
        }
    }

	protected void setPathToDriverIfExistsAndIsExecutable(final String relativePathToDriver, final String driverExeProperty) {
		final File driver = new File(relativePathToDriver);
		if (driver.exists() && driver.canExecute()) {
		    System.setProperty(driverExeProperty, driver.getAbsolutePath());
		} else {
		    throw new IllegalArgumentException(String.format("Driver not found or is not executable in %s", relativePathToDriver));
		}
	}

}

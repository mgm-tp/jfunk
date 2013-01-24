/*
 *  Copyright (c) mgm technology partners GmbH, Munich.
 *
 *  See the copyright.txt file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.web;

import static com.mgmtp.jfunk.common.util.Varargs.va;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.io.FileUtils.writeStringToFile;

import java.io.File;
import java.util.EnumMap;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mgmtp.jfunk.common.JFunkConstants;
import com.mgmtp.jfunk.common.config.ScriptScoped;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.common.util.SaveOutput;
import com.mgmtp.jfunk.core.config.ModuleArchiveDir;
import com.mgmtp.jfunk.web.util.DumpFileCreator;
import com.mgmtp.jfunk.web.util.HtmlValidatorUtil;
import com.mgmtp.jfunk.web.util.WebDriverUtils;

/**
 * Default implementation for WebDriverEventListener.
 * 
 * @version $Id$
 */
@ScriptScoped
public class JFunkWebDriverEventListener implements WebDriverEventListener {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final EnumMap<SaveOutput, Boolean> saveOutputMap;
	private final Configuration config;
	private final Provider<File> moduleArchiveDirProvider;
	private final Provider<DumpFileCreator> dumpFileCreatorProvider;

	@Inject
	public JFunkWebDriverEventListener(final Configuration config, @ModuleArchiveDir final Provider<File> moduleArchiveDirProvider,
			final Provider<DumpFileCreator> dumpFileCreatorProvider) {
		this.config = config;
		this.moduleArchiveDirProvider = moduleArchiveDirProvider;
		this.dumpFileCreatorProvider = dumpFileCreatorProvider;
		this.saveOutputMap = new EnumMap<SaveOutput, Boolean>(SaveOutput.class);
		for (SaveOutput saveOutput : SaveOutput.values()) {
			// active flag for every output type
			saveOutputMap.put(saveOutput,
					config.getBoolean(JFunkConstants.ARCHIVE_INCLUDE + saveOutput.getIdentifier(), saveOutput.isActiveByDefault()));
		}
	}

	@Override
	public void beforeNavigateTo(final String url, final WebDriver driver) {
		// unused
	}

	@Override
	public void afterNavigateTo(final String url, final WebDriver driver) {
		savePage(driver, "afterNavigateTo", url);

		if (WebDriverUtils.isHtmlUnitDriver(driver) && config.getBoolean(WebConstants.HTMLUNIT_SAVE_COMPLETE, false)) {
			Page page = WebDriverUtils.getHtmlUnitDriverWebClient(driver).getCurrentWindow().getEnclosedPage();

			if (page instanceof HtmlPage && page.getUrl() != WebClient.URL_ABOUT_BLANK) {
				String urlString = page.getUrl().toString();
				log.debug("Dumping HTML file with resources: " + urlString);

				DumpFileCreator dumpFileCreator = dumpFileCreatorProvider.get();
				File htmlFile = dumpFileCreator.createDumpFile(new File(moduleArchiveDirProvider.get(), "htmldump"), "html", urlString, null);
				try {
					if (htmlFile.exists()) {
						htmlFile.delete();
					}
					((HtmlPage) page).save(htmlFile);
				} catch (Exception ex) {
					log.error("Error dumping HTML file from URL: " + urlString, ex);
				}
			}
		}
	}

	@Override
	public void beforeNavigateBack(final WebDriver driver) {
		// unused
	}

	@Override
	public void afterNavigateBack(final WebDriver driver) {
		// unused
	}

	@Override
	public void beforeNavigateForward(final WebDriver driver) {
		// unused
	}

	@Override
	public void afterNavigateForward(final WebDriver driver) {
		// unused
	}

	@Override
	public void beforeFindBy(final By by, final WebElement element, final WebDriver driver) {
		// unused
	}

	@Override
	public void afterFindBy(final By by, final WebElement element, final WebDriver driver) {
		// unused
	}

	@Override
	public void beforeClickOn(final WebElement element, final WebDriver driver) {
		// unused
	}

	@Override
	public void afterClickOn(final WebElement element, final WebDriver driver) {
		savePage(driver, "afterClick", element.toString());
	}

	@Override
	public void beforeChangeValueOf(final WebElement element, final WebDriver driver) {
		// unused
	}

	@Override
	public void afterChangeValueOf(final WebElement element, final WebDriver driver) {
		savePage(driver, "afterChangeValueOf", element.toString());
	}

	@Override
	public void beforeScript(final String script, final WebDriver driver) {
		// unused
	}

	@Override
	public void afterScript(final String script, final WebDriver driver) {
		// unused
	}

	@Override
	public void onException(final Throwable throwable, final WebDriver driver) {
		savePage(driver, "onException", null);
	}

	/**
	 * Saves the currently displayed browser window. The page title is used for the filename -
	 * preceded by some identifying information (thread, counter). Pages of the same type are
	 * collected inside the same subdirectory. The subdirectory uses @ * eOutput#getIdentifier()}
	 * for its name.
	 * 
	 * @param action
	 *            the event which triggered to save the page. Will be included in the filename.
	 * @param triggeredBy
	 *            the object which triggered the event (e.g. a button or a link)
	 */
	protected void savePage(final WebDriver driver, final String action, final String triggeredBy) {
		File moduleArchiveDir = moduleArchiveDirProvider.get();
		if (moduleArchiveDir == null) {
			return;
		}

		for (SaveOutput saveOutput : SaveOutput.values()) {
			boolean saveSwitch = saveOutputMap.get(saveOutput);
			if (!saveSwitch) {
				// Saving is disabled by property
				continue;
			}

			File f = null;
			try {
				f = dumpFileCreatorProvider.get().createDumpFile(new File(moduleArchiveDir, saveOutput.getIdentifier()),
						saveOutput.getExtension(), driver.getCurrentUrl(), action);

				if (f == null) {
					return;
				}

				switch (saveOutput) {
					case HTML:
						StringBuilder html = new StringBuilder();
						html.append("<!-- Requested URL: ");
						html.append(driver.getCurrentUrl());
						html.append(" -->");
						html.append(IOUtils.LINE_SEPARATOR);
						html.append(driver.getPageSource());
						writeStringToFile(f, html.toString(), "UTF-8");
						copyFile(f, new File(moduleArchiveDir, JFunkConstants.LASTPAGE_HTML));
						log.debug("Saving page: filename={}, action={}, trigger={}, response={}",
								va(f.getName(), action, triggeredBy, driver.getCurrentUrl()));
						break;
					case PNG:
						if (driver instanceof TakesScreenshot) {
							File tmpFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
							if (tmpFile != null) {
								copyFile(tmpFile, f);
								log.debug("Saving page: filename={}, action={}, trigger={}, response={}",
										va(f.getName(), action, triggeredBy, driver.getCurrentUrl()));
								deleteQuietly(tmpFile);
							}
						}
						break;
					case HTML_VALIDATION:
						/*
						 * JFunkWebDriver.getPageSource() doesn't return the complete page source
						 * e.g. DOCTYPE is missing. Therefore we are using a more complicated way to
						 * retrieve the "real" page source. However, this only works when using
						 * HtmlUnitDriver.
						 */
						if (WebDriverUtils.isHtmlUnitDriver(driver)) {
							String content = ((HtmlPage) WebDriverUtils.getHtmlUnitDriverWebClient(driver).getCurrentWindow().getEnclosedPage())
									.getWebResponse().getContentAsString();
							writeStringToFile(f, content, "UTF-8");
							HtmlValidatorUtil.validateHtml(f.getParentFile(), config, f);
						}
						break;
					default:
						throw new IllegalStateException("unknown enum constant");
				}
			} catch (Exception ex) {
				log.error("Could not save file: {}. {}", f, ex.getMessage());
			}
		}
	}
}
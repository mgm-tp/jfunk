package com.mgmtp.jfunk.web.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.difference;

import java.util.Set;

import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.WebClient;
import com.google.common.collect.Sets.SetView;
import com.mgmtp.jfunk.web.JFunkHtmlUnitWebDriver;

/**
 * Provides {@link WebDriver} utility methods.
 * 
 * @author rnaegele
 * @version $Id$
 */
public final class WebDriverUtils {

	private WebDriverUtils() {
		// don't allow instantiation
	}

	/**
	 * Returns the wrapped {@link WebDriver} instance if the given one is an
	 * {@link EventFiringWebDriver}.
	 * 
	 * @param webDriver
	 *            the {@link WebDriver} instance
	 * @return the wrapped {@link WebDriver} instance
	 */
	public static WebDriver getWrappedDriver(final WebDriver webDriver) {
		if (webDriver instanceof EventFiringWebDriver) {
			return ((EventFiringWebDriver) webDriver).getWrappedDriver();
		}

		return webDriver;
	}

	/**
	 * Returns {@code true} if the specified {@link WebDriver} is an {@link HtmlUnitDriver}. Before
	 * checking, this method calls {@link #getWrappedDriver(WebDriver)} on the given
	 * {@link WebDriver} instance.
	 * 
	 * @param webDriver
	 *            the web driver
	 * @return {@code true} if the specified {@link WebDriver} is an {@link HtmlUnitDriver}
	 */
	public static boolean isHtmlUnitDriver(final WebDriver webDriver) {
		WebDriver driver = getWrappedDriver(webDriver);
		return driver instanceof JFunkHtmlUnitWebDriver;
	}

	/**
	 * Returns {@code true} if the specified {@link WebDriver} is an {@link RemoteWebDriver}. Before
	 * checking, this method calls {@link #getWrappedDriver(WebDriver)} on the given
	 * {@link WebDriver} instance.
	 * 
	 * @param webDriver
	 *            the web driver
	 * @return {@code true} if the specified {@link WebDriver} is an {@link RemoteWebDriver}
	 */
	public static boolean isRemoteWebDriver(final WebDriver webDriver) {
		WebDriver driver = getWrappedDriver(webDriver);
		return driver instanceof RemoteWebDriver;
	}

	/**
	 * Gets the {@link WebClient} of the specified {@link WebDriver}. Calls
	 * {@link #getWrappedDriver(WebDriver)} on the given {@link WebDriver} and checks that it is a
	 * {@link JFunkHtmlUnitWebDriver}.
	 * 
	 * @param webDriver
	 *            the web driver
	 * @return the {@link WebClient}
	 * @throws IllegalArgumentException
	 *             if the specified {@link WebDriver} is no {@link JFunkHtmlUnitWebDriver}
	 */
	public static WebClient getHtmlUnitDriverWebClient(final WebDriver webDriver) {
		WebDriver driver = webDriver;
		driver = getWrappedDriver(driver);
		checkArgument(driver instanceof JFunkHtmlUnitWebDriver, "Specified WebDriver is no JFunkHtmlUnitDriver: " + driver);
		return ((JFunkHtmlUnitWebDriver) driver).getWebClient();
	}

	/**
	 * Switches to the newly open window. The window to switch to is determined by diffing the given
	 * {@code existingWindowHandles} with the current ones. The difference must be exactly one
	 * window handle which is then used to switch to.
	 * 
	 * @param webDriver
	 *            the WebDriver
	 * @param existingWindowHandles
	 *            a set of window handles that does not contain the handle of the newly opened one,
	 *            i. e. this set should be obtained by calling {@link WebDriver#getWindowHandles()}
	 *            before the new window is opened
	 * @param timeoutSeconds
	 *            the timeout in seconds to wait for the new window to open
	 * @return the handle of the newly opened window
	 */
	public static String switchToNewWindow(final WebDriver webDriver, final Set<String> existingWindowHandles, final long timeoutSeconds) {
		BasePredicate<WebDriver, String> predicate = new BasePredicate<WebDriver, String>() {
			private String result;

			@Override
			protected boolean doApply(final WebDriver input) {
				Set<String> newWindowHandles = webDriver.getWindowHandles();
				SetView<String> newWindows = difference(newWindowHandles, existingWindowHandles);
				if (newWindows.isEmpty()) {
					throw new NotFoundException("No new window found.");
				}
				result = getOnlyElement(newWindows);
				return true;
			}

			@Override
			public String getResult() {
				return result;
			}
		};

		WebDriverWait wait = new WebDriverWait(webDriver, timeoutSeconds);
		wait.until(predicate);

		String newHandle = predicate.getResult();
		webDriver.switchTo().window(newHandle);
		return newHandle;
	}
}

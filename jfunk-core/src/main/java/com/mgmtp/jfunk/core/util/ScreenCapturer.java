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
package com.mgmtp.jfunk.core.util;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.io.Files.createParentDirs;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.core.config.ModuleArchiveDir;
import com.mgmtp.jfunk.core.event.AbstractBaseEvent;
import com.mgmtp.jfunk.core.event.AfterModuleEvent;

/**
 * <p>
 * Class for creating screenshots.
 * </p>
 * <p>
 * <b>Note:</b><br />
 * When jFunk is running multiple threads, i. e. multiple browser windows are open, there is no
 * guarantee that a screenshot captures the correct browser window because the current thread's
 * browser window may not be the top-level one.
 * </p>
 * <p>
 * The static methods of the class may be used independently. However, the class may also be
 * registered with jFunk's {@link EventBus} using {@link ScreenCapturerModule} in order to configure
 * automatic screenshots.
 * </p>
 * <p>
 * In order to make jFunk create automatic screenshots whenever an {@link AbstractBaseEvent} is
 * posted on the {@link EventBus}, the Guice module {@link ScreenCapturerModule} must be installed
 * and the actual {@link AbstractBaseEvent} descendants eligible for capturing the screen must be
 * configured as a comma-separated list using the property {@code screenshot.event.classes}. In the
 * case of an error, a screenshot is always taken on {@link AfterModuleEvent} if the property
 * {@code screenshot.on.error} is set to {@code true}.
 * </p>
 * <p>
 * <b>Example:</b>
 * 
 * <pre>
 * screenshot.event.classes = com.mgmtp.jfunk.core.event.AfterStepEvent,com.mgmtp.jfunk.core.event.AfterModuleEvent
 * screenshot.on.error = true
 * </pre>
 * 
 * </p>
 * 
 * @author rnaegele
 * @since 3.1.0
 */
@Singleton
public class ScreenCapturer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ScreenCapturer.class);
	private static final String SCREENSHOT_PATH_FORMAT = "screenshots/%04d_%s.png";

	private final Provider<File> moduleArchiveDirProvider;
	private final Set<Class<? extends AbstractBaseEvent>> screenCaptureEvents;
	private final Provider<MutableInt> counterProvider;
	private final boolean captureScreenOnError;

	@Inject
	public ScreenCapturer(@ModuleArchiveDir final Provider<File> moduleArchiveDirProvider,
			final Set<Class<? extends AbstractBaseEvent>> screenCaptureEvents, final Provider<MutableInt> counterProvider,
			@CaptureScreenOnError final boolean captureScreenOnError) {
		this.moduleArchiveDirProvider = moduleArchiveDirProvider;
		this.screenCaptureEvents = screenCaptureEvents;
		this.counterProvider = counterProvider;
		this.captureScreenOnError = captureScreenOnError;
	}

	/**
	 * Captures the whole screen and saves it as png to the specified file. The file must have the
	 * extension {@code png}. Any necessary but non-existent parent directories of the specified
	 * file are created.
	 * 
	 * @param file
	 *            the file to save the screenshot to
	 */
	public static void captureScreen(final File file) {
		Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		captureScreenRect(file, rectangle);
	}

	/**
	 * Captures the specified rectangle and saves it as png to the specified file. The file must
	 * have the extension {@code png}. Any necessary but non-existent parent directories of the
	 * specified file are created.
	 * 
	 * @param file
	 *            the file to save the screenshot to
	 * @param rectangle
	 *            the rectangle to capture in screen coordinates
	 */
	public static void captureScreenRect(final File file, final Rectangle rectangle) {
		checkState(file.getName().endsWith("png"), "Screenshot file must have extension 'png': %s", file);
		LOGGER.trace("Creating screenshot: {}", file);

		try {
			createParentDirs(file);
			BufferedImage capture = new Robot().createScreenCapture(rectangle);
			ImageIO.write(capture, "png", file);
		} catch (AWTException ex) {
			throw new JFunkException("Error capturing screen", ex);
		} catch (IOException ex) {
			throw new JFunkException("Error saving screenshot", ex);
		}
	}

	/**
	 * Event handler method used by the {@link EventBus}. If configured for the given event's class,
	 * a screenshot is taken and stored in folder {@code screenshots} in the current module's
	 * archive directory. The screenshot images are named by the event class' simple name, prefixed
	 * with a left-padded four-digit integer counter (format: {@code %04d_%s.png}).
	 * 
	 * @param event
	 *            the event
	 */
	@Subscribe
	@AllowConcurrentEvents
	public void handleEvent(final AbstractBaseEvent event) {
		Class<? extends AbstractBaseEvent> clazz = event.getClass();

		boolean errorCapture = captureScreenOnError(event);
		boolean eventConfigured = screenCaptureEvents.contains(clazz);
		if (eventConfigured || errorCapture) {
			String screenshotName = clazz.getSimpleName();
			if (errorCapture) {
				screenshotName += "_error";
			}
			captureAndArchiveScreen(screenshotName);
		}
	}

	private boolean captureScreenOnError(final AbstractBaseEvent event) {
		if (captureScreenOnError && event instanceof AfterModuleEvent) {
			return !((AfterModuleEvent) event).isSuccess();
		}

		return false;
	}

	/**
	 * Takes a screenshot and stores it in folder {@code screenshots} in the current module's
	 * archive directory. The screenshot image is named by the specified screenshot name, prefixed
	 * with a left-padded four-digit integer counter (format: {@code %04d_%s.png}).
	 * 
	 * @param screenshotName
	 *            the name of the screenshot
	 */
	public void captureAndArchiveScreen(final String screenshotName) {
		MutableInt counter = counterProvider.get();
		String relativePath = String.format(SCREENSHOT_PATH_FORMAT, counter.intValue(), screenshotName);
		captureScreen(new File(moduleArchiveDirProvider.get(), relativePath));
		counter.increment();
	}
}

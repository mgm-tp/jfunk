package com.mgmtp.jfunk.web.step;

import org.openqa.selenium.By;

/**
 * @author rnaegele
 * @version $Id$
 */
public class SendKeysStep extends WebDriverStep {

	private final By by;
	private final CharSequence keysToSend;

	public SendKeysStep(final By by, final CharSequence keysToSend) {
		this.by = by;
		this.keysToSend = keysToSend;
	}

	@Override
	public void execute() {
		getWebDriver().findElement(by).sendKeys(keysToSend);
	}
}

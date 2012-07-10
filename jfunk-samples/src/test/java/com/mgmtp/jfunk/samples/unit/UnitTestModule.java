package com.mgmtp.jfunk.samples.unit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.mgmtp.jfunk.core.module.TestModuleImpl;
import com.mgmtp.jfunk.web.step.CheckHtml4Pattern;
import com.mgmtp.jfunk.web.step.JFunkWebElement;
import com.mgmtp.jfunk.web.step.LoadPage;
import com.mgmtp.jfunk.web.step.SendKeysStep;

/**
 * @author rnaegele
 * @version $Id$
 */
public class UnitTestModule extends TestModuleImpl {

	private final String searchTerm;

	UnitTestModule(final String searchTerm) {
		super("google", "google");
		this.searchTerm = searchTerm;
	}

	@Override
	protected void executeSteps() {
		String url = "http://www.google.com";

		executeSteps(
				//Load page
				new LoadPage(url),

				// Enter form value (form name=f, form field=q)
				new JFunkWebElement(By.name("q"), searchTerm),

				// Click submit button
				new SendKeysStep(By.name("q"), Keys.ENTER),

				// Search for text on page
				new CheckHtml4Pattern(String.format("(?s).*%s.*", searchTerm)));
	}
}
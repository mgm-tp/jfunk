package com.mgmtp.jfunk.samples.forminput;

import static com.mgmtp.jfunk.samples.forminput.ForminputConstants.BOOLEAN_PROPERTY;
import static com.mgmtp.jfunk.samples.forminput.ForminputConstants.DOUBLE_PROPERTY;
import static com.mgmtp.jfunk.samples.forminput.ForminputConstants.INTEGER_IN_RANGE_PROPERTY;
import static com.mgmtp.jfunk.samples.forminput.ForminputConstants.INTEGER_PROPERTY;
import static com.mgmtp.jfunk.samples.forminput.ForminputConstants.LOCALE_SELECT;
import static com.mgmtp.jfunk.samples.forminput.ForminputConstants.NUMBERS_GROUP;
import static com.mgmtp.jfunk.samples.forminput.ForminputConstants.NUMBER_RADIO_CHOICE;
import static com.mgmtp.jfunk.samples.forminput.ForminputConstants.OPERATION;
import static com.mgmtp.jfunk.samples.forminput.ForminputConstants.PHONE_NUMBER_US;
import static com.mgmtp.jfunk.samples.forminput.ForminputConstants.STRING_PROPERTY;
import static com.mgmtp.jfunk.samples.forminput.ForminputConstants.URL_PROPERTY;

import org.openqa.selenium.By;

import com.mgmtp.jfunk.core.reporting.Reported;
import com.mgmtp.jfunk.core.step.base.ComplexStep;
import com.mgmtp.jfunk.core.step.base.StepMode;
import com.mgmtp.jfunk.web.step.JFunkWebElement;

/**
 * This {@link ComplexStep} represents the whole Wicket forminput example page. It is used for
 * reading from and writing to all input fields on the page.
 * 
 * @version $Id$
 */
@Reported
public class ForminputPage extends ComplexStep {

	public ForminputPage(final StepMode stepMode) {
		super(stepMode);
	}

	@Override
	public void executeSteps() {
		if (StepMode.CHECK_VALUE.equals(stepMode)) {
			/*
			 * Check the default values of all fields. As the default entries are "real" entries
			 * (and not an empty field) the StepMode CHECK_VALUE is used.
			 */
			executeSteps(
					new JFunkWebElement(By.name(LOCALE_SELECT), "0", StepMode.CHECK_VALUE),
					new JFunkWebElement(By.name(STRING_PROPERTY), "test", StepMode.CHECK_VALUE),
					new JFunkWebElement(By.name(INTEGER_PROPERTY), "100", StepMode.CHECK_VALUE),
					new JFunkWebElement(By.name(DOUBLE_PROPERTY), "20.5", StepMode.CHECK_VALUE),
					new JFunkWebElement(By.name(BOOLEAN_PROPERTY), "on", StepMode.CHECK_VALUE),
					new JFunkWebElement(By.name(INTEGER_IN_RANGE_PROPERTY), "50", StepMode.CHECK_VALUE),
					new JFunkWebElement(By.name(URL_PROPERTY), "http://wicket.apache.org", StepMode.CHECK_VALUE),
					new JFunkWebElement(By.name(PHONE_NUMBER_US), "(123) 456-1234", StepMode.CHECK_VALUE),
					new JFunkWebElement(By.name(NUMBER_RADIO_CHOICE), "0", StepMode.CHECK_VALUE),
					new JFunkWebElement(By.name(NUMBERS_GROUP), "radio0", StepMode.CHECK_VALUE));
		} else {

			executeSteps(
					new JFunkWebElement(By.name(LOCALE_SELECT), LOCALE_SELECT, OPERATION, stepMode),
					new JFunkWebElement(By.name(STRING_PROPERTY), STRING_PROPERTY, OPERATION, stepMode),
					new JFunkWebElement(By.name(INTEGER_PROPERTY), INTEGER_PROPERTY, OPERATION, stepMode),
					new JFunkWebElement(By.name(DOUBLE_PROPERTY), DOUBLE_PROPERTY, OPERATION, stepMode),
					new JFunkWebElement(By.name(BOOLEAN_PROPERTY), BOOLEAN_PROPERTY, OPERATION, stepMode),
					new JFunkWebElement(By.name(INTEGER_IN_RANGE_PROPERTY), INTEGER_IN_RANGE_PROPERTY, OPERATION, stepMode),
					new JFunkWebElement(By.name(URL_PROPERTY), URL_PROPERTY, OPERATION, stepMode),
					new JFunkWebElement(By.name(PHONE_NUMBER_US), PHONE_NUMBER_US, OPERATION, stepMode),
					new JFunkWebElement(By.name(NUMBER_RADIO_CHOICE), NUMBER_RADIO_CHOICE, OPERATION, stepMode),
					new JFunkWebElement(By.name(NUMBERS_GROUP), NUMBERS_GROUP, OPERATION, stepMode));
		}
	}
}
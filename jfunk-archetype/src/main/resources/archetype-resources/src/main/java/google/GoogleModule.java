#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.google;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.mgmtp.jfunk.core.module.TestModuleImpl;
import com.mgmtp.jfunk.core.step.base.StepMode;
import com.mgmtp.jfunk.web.step.CheckHtml4Pattern;
import com.mgmtp.jfunk.web.step.JFunkWebElement;
import com.mgmtp.jfunk.web.step.LoadPage;
import com.mgmtp.jfunk.web.step.SendKeysStep;

public class GoogleModule extends TestModuleImpl {

	public GoogleModule() {
		super("google");
	}

	@Override
	protected void executeSteps() {
		executeSteps(
				new LoadPage("http://www.google.com"),
				new JFunkWebElement(By.name("q"), "searchTerm", getDataSetKey(), StepMode.SET_VALUE),
				new SendKeysStep(By.name("q"), Keys.ENTER),
				new CheckHtml4Pattern("(?s).*" + getDataSet().getValue("searchTerm") + ".*"));
	}
}

package com.mgmtp.jfunk.server.domain;

import static org.testng.Assert.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.testng.annotations.Test;

/**
 * @author rnaegele
 * @version $Id$
 */
public class ScriptParamsTest {

	@Test
	public void testScriptParams() throws JAXBException {
		ScriptParams params = new ScriptParams();

		ScriptParam param = new ScriptParam();
		param.setName("foo");
		param.setValue("fooValue");

		params.getScriptParams().add(param);

		JAXBContext context = JAXBContext.newInstance(ScriptParams.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		StringWriter sw = new StringWriter();

		m.marshal(params, sw);

		Unmarshaller um = context.createUnmarshaller();
		ScriptParams paramsNew = (ScriptParams) um.unmarshal(new StringReader(sw.toString()));

		assertEquals(paramsNew.getScriptParams().size(), 1);

		ScriptParam paramNew = paramsNew.getScriptParams().get(0);
		assertEquals(paramNew.getName(), param.getName());
		assertEquals(paramNew.getValue(), param.getValue());
	}
}

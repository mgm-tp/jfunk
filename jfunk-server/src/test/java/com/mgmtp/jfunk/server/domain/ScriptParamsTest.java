/*
 * Copyright (c) 2013 mgm technology partners GmbH
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

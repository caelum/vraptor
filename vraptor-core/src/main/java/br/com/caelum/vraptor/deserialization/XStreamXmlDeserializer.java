/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.deserialization;

import java.io.InputStream;
import java.lang.reflect.Method;

import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

import com.thoughtworks.xstream.XStream;

/**
 * XStream based Xml Deserializer
 * @author Lucas Cavalcanti
 * @author Cecilia Fernandes
 * @since 3.0.2
 */
@ApplicationScoped
public class XStreamXmlDeserializer implements XmlDeserializer {

	private final ParameterNameProvider provider;

	public XStreamXmlDeserializer(ParameterNameProvider provider) {
		this.provider = provider;
	}

	public Object[] deserialize(InputStream inputStream, ResourceMethod method) {
		Method javaMethod = method.getMethod();
		Class<?>[] types = javaMethod.getParameterTypes();
		if (types.length != 1) {
			throw new IllegalArgumentException("Methods that consumes xml must receive just one argument: the xml root element");
		}
		XStream xStream = getXStream();
		String[] names = provider.parameterNamesFor(javaMethod);
		xStream.alias(names[0], types[0]);
		Object deserialized = xStream.fromXML(inputStream);
		return new Object[] {deserialized};
	}

	protected XStream getXStream() {
		return new XStream();
	}

}

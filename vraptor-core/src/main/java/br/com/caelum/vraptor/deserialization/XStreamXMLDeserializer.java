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
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.resource.ResourceMethod;

import br.com.caelum.vraptor.serialization.xstream.XStreamBuilder;
import com.thoughtworks.xstream.XStream;

/**
 * XStream based Xml Deserializer
 * @author Lucas Cavalcanti
 * @author Cecilia Fernandes
 * @author Guilherme Silveira
 * @author Rafael Viana
 * @since 3.0.2
 */
@Component
public class XStreamXMLDeserializer implements XMLDeserializer {

	private final ParameterNameProvider provider;
	private final XStreamBuilder builder;

	public XStreamXMLDeserializer(ParameterNameProvider provider, XStreamBuilder builder) {
		this.provider = provider;
		this.builder = builder;
	}

	public Object[] deserialize(InputStream inputStream, ResourceMethod method) {
		Method javaMethod = method.getMethod();
		Class<?>[] types = javaMethod.getParameterTypes();
		if (types.length == 0) {
			throw new IllegalArgumentException("Methods that consumes xml must receive just one argument: the xml root element");
		}
		XStream xStream = getConfiguredXStream(javaMethod, types);

		Object[] params = new Object[types.length];
		
		chooseParam(types, params, xStream.fromXML(inputStream));

		return params;
	}

	/**
	 * @return an xstream instance already configured.
	 */
	public XStream getConfiguredXStream(Method javaMethod, Class<?>[] types) {
		XStream xStream = getXStream();
		aliasParams(javaMethod, types, xStream);
		return xStream;
	}

	private void chooseParam(Class<?>[] types, Object[] params, Object deserialized) {
		for (int i = 0; i < types.length; i++) {
			if (types[i].isInstance(deserialized)) {
				params[i] = deserialized;
			}
		}
	}

	private void aliasParams(Method method, Class<?>[] types, XStream deserializer) {
		String[] names = provider.parameterNamesFor(method);
		for (int i = 0; i < names.length; i++) {
			deserializer.alias(names[i], types[i]);
		}
	}

	/**
	 * Extension point to configure your xstream instance.
	 * @return the configured xstream instance
     * @deprecated prefer overriding XStreamBuilder
	 */
	@Deprecated
	protected XStream getXStream() {
		return builder.xmlInstance();
	}
	
}

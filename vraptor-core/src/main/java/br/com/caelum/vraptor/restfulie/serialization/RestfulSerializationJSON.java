/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource - guilherme.silveira@caelum.com.br
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.restfulie.serialization;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.config.Configuration;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.restfulie.Restfulie;
import br.com.caelum.vraptor.serialization.ProxyInitializer;
import br.com.caelum.vraptor.serialization.xstream.XStreamBuilder;
import br.com.caelum.vraptor.serialization.xstream.XStreamJSONSerialization;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;

/**
 * Custom serialization process provides a way to add links to your resource representations.
 * @author guilherme silveira
 * @author ac de souza
 */
@Component
@RequestScoped
public class RestfulSerializationJSON extends XStreamJSONSerialization {

	private final Restfulie restfulie;
	private final Configuration config;

	public RestfulSerializationJSON(HttpServletResponse response, TypeNameExtractor extractor, Restfulie restfulie, Configuration config, ProxyInitializer initializer, XStreamBuilder builder) {
		super(response,extractor,initializer, builder);
		this.restfulie = restfulie;
		this.config = config;
	}

	/**
	 * You can override this method for configuring XStream before serialization.
	 * It configures the xstream instance with a link converter for all StateResource implementations.
	 */
	@Override
	protected XStream getXStream() {
		XStream xStream = super.getXStream();
		MethodValueSupportConverter converter = new MethodValueSupportConverter(new ReflectionConverter(xStream.getMapper(), xStream.getReflectionProvider()));
		xStream.registerConverter(new LinkConverterJSON(converter, restfulie, config));
		return xStream;
	}
}

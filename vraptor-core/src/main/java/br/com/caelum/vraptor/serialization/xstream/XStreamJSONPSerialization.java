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
package br.com.caelum.vraptor.serialization.xstream;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.JSONPSerialization;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.ProxyInitializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.view.ResultException;

/**
 *
 * Default implementation for JSONPSerialization that decorates default jsonSerialization output
 * adding the callback padding
 *
 * @author Lucas Cavalcanti
 * @author Pedro Matiello
 * @since 3.3.0
 *
 */
public class XStreamJSONPSerialization implements JSONPSerialization {

	private final HttpServletResponse response;
	private final TypeNameExtractor extractor;
	private final ProxyInitializer initializer;
	private final XStreamBuilder builder;

	public XStreamJSONPSerialization(HttpServletResponse response, TypeNameExtractor extractor, ProxyInitializer initializer, XStreamBuilder builder) {
		this.response = response;
		this.extractor = extractor;
		this.initializer = initializer;
		this.builder = builder;
	}

	public JSONSerialization withCallback(final String callbackName) {
		return new XStreamJSONSerialization(response, extractor, initializer, builder) {
			@Override
			protected SerializerBuilder getSerializer() {
				try {
					final PrintWriter writer = response.getWriter();
					final StringWriter out = new StringWriter();
					return new XStreamSerializer(super.getXStream(), new PrintWriter(out), extractor, initializer) {
						@Override
						public void serialize() {
							super.serialize();
							writer.append(callbackName).append("(").append(out.getBuffer()).append(")");
							writer.close();
						}
					};
				} catch (IOException e) {
					throw new ResultException("Unable to serialize data", e);
				}
			}
		};
	}

}

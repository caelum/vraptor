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
package br.com.caelum.vraptor.serialization;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.config.Configuration;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.rest.Restfulie;
import br.com.caelum.vraptor.view.ResultException;

/**
 * The default implementation of xml serialization.<br/>
 * It will set the content type to application/xml by default.
 *
 * @author guilherme silveira
 * @since 3.0.3
 */
@Component
@RequestScoped
public class DefaultXMLSerialization implements XMLSerialization {

	private final HttpServletResponse response;
	private final Restfulie restfulie;
	private final Configuration config;

	public DefaultXMLSerialization(HttpServletResponse response, Restfulie restfulie, Configuration config) {
		this.response = response;
		this.restfulie = restfulie;
		this.config = config;
	}

	public <T> Serializer from(T object) {
		response.setContentType("application/xml");
		try {
			Serializer serializer = new DefaultXMLSerializer(null, response.getWriter(), restfulie, config).from(object);
			return serializer;
		} catch (IOException e) {
			throw new ResultException("Unable to serialize data",e);
		}
	}

}

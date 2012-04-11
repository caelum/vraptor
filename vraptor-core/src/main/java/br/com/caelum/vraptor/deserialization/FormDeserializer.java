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
import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.deserialization.Deserializer;
import br.com.caelum.vraptor.deserialization.Deserializes;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Message;

/**
 * 
 * @author Celso Dantas
 *
 */
@Deserializes("application/x-www-form-urlencoded")
public class FormDeserializer implements Deserializer {

	private ParametersProvider provider;
	private Localization localization;
	
	public FormDeserializer(ParametersProvider provider, Localization localization) {
		this.provider = provider;
		this.localization = localization;
	}

	public Object[] deserialize(InputStream inputStream, ResourceMethod method) {
		List<Message> errors = new ArrayList<Message>();
		return provider.getParametersFor(method, errors, localization.getBundle());

	}
}

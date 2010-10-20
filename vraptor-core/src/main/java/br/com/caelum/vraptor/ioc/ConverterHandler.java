/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
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
package br.com.caelum.vraptor.ioc;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.core.Converters;

@ApplicationScoped
public class ConverterHandler implements StereotypeHandler {

	private static final Logger logger = LoggerFactory.getLogger(ConverterHandler.class);

	private Converters converters;

	public ConverterHandler(Converters converters) {
		this.converters = converters;
	}

	public void handle(Class<?> annotatedType) {
		if (!(Converter.class.isAssignableFrom(annotatedType))) {
			throw new VRaptorException("converter does not implement Converter");
		}
		if (BaseComponents.getBundledConverters().contains(annotatedType)) {
			logger.debug("Ignoring handling default converter {}", annotatedType);
			return;
		}
		@SuppressWarnings("unchecked")
		Class<? extends Converter<?>> converterType = (Class<? extends Converter<?>>) annotatedType;

		converters.register(converterType);
	}

	public Class<? extends Annotation> stereotype() {
		return Convert.class;
	}
}

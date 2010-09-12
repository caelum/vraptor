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
package br.com.caelum.vraptor.vraptor2;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.TwoWayConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.DefaultConverters;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Container;

/**
 * An adaptor between vraptor2 converter list and vraptor 3.<br>
 * If you use the default converters delegate, it requires the register
 * container in order to register new converters.
 *
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class VRaptor2Converters implements Converters {

	private  Converters delegateConverters;
	private final List<org.vraptor.converter.Converter> converterList = new ArrayList<org.vraptor.converter.Converter>();

	public VRaptor2Converters(Config config, Container container) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		// probably vraptor3 converters
		this.delegateConverters = new DefaultConverters(container);
		List<String> list = config.getConverters();
		for (String l : list) {
			@SuppressWarnings("unchecked")
			Class<? extends org.vraptor.converter.Converter> converterType = (Class<? extends org.vraptor.converter.Converter>) Class
					.forName(l);
			converterList.add(converterType.newInstance());
		}
	}

	// for unit tests only
	void setDelegateConverters(Converters delegateConverters) {
		this.delegateConverters = delegateConverters;
	}

	public Converter<?> to(Class<?> type) {
		Converter<?> vraptor2Convterter = findVRaptor2Converter(type);
		return vraptor2Convterter == null ? delegateConverters.to(type) : vraptor2Convterter;
	}

	private Converter<?> findVRaptor2Converter(Class<?> type) {
		for (org.vraptor.converter.Converter converter : converterList) {
			for (Class<?> supported : converter.getSupportedTypes()) {
				if (supported.isAssignableFrom(type)) {
					return new ConverterWrapper(converter);
				}
			}
		}
		return null;
	}

	public void register(Class<? extends Converter<?>> converterClass) {
		delegateConverters.register(converterClass);
	}

	public boolean existsFor(Class<?> type) {
		return existsVRaptor2ConverterFor(type) || existsVRaptor3ConverterFor(type);
	}

	private boolean existsVRaptor3ConverterFor(Class<?> type) {
		return delegateConverters.existsFor(type);
	}

	private boolean existsVRaptor2ConverterFor(Class<?> type) {
		return findVRaptor2Converter(type) != null;
	}

	public boolean existsTwoWayFor(Class<?> type) {
		return false;
	}

	public TwoWayConverter<?> twoWayConverterFor(Class<?> type) {
		throw new UnsupportedOperationException(
			"no new features for VRaptor 2 compatibility, sorry");
	}

}

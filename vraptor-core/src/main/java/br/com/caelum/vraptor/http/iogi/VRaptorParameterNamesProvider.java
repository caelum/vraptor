/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.http.iogi;

import java.lang.reflect.AccessibleObject;
import java.util.Arrays;
import java.util.List;

import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.ioc.Component;

/**
 * An adapter for iogi's parameterNamesProvider
 *
 * @author Lucas Cavalcanti
 * @since
 *
 */
@Component
public class VRaptorParameterNamesProvider implements br.com.caelum.iogi.spi.ParameterNamesProvider {
	private final ParameterNameProvider parameterNameProvider;

	public VRaptorParameterNamesProvider(ParameterNameProvider parameterNameProvider) {
		this.parameterNameProvider = parameterNameProvider;
	}

	public List<String> lookupParameterNames(AccessibleObject methodOrConstructor) {
		return Arrays.asList(parameterNameProvider.parameterNamesFor(methodOrConstructor));
	}
}
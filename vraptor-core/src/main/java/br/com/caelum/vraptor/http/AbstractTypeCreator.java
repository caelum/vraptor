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
package br.com.caelum.vraptor.http;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.vraptor2.Info;

/**
 * A class that provides default intended implementation for instanceWithParameters method.
 * TypeCreator implementations should extend this class.
 * @author Lucas Cavalcanti
 *
 */
public abstract class AbstractTypeCreator implements TypeCreator {

	private final ParameterNameProvider provider;

	public AbstractTypeCreator(ParameterNameProvider provider) {
		this.provider = provider;
	}

	public final Object instanceWithParameters(ResourceMethod method, Object... parameters) {
		String[] names = provider.parameterNamesFor(method.getMethod());
		Class<?> parameterType = typeFor(method);
		try {
			Object root = parameterType.getConstructor().newInstance();
			for (int i = 0; i < names.length; i++) {
				Method setter = findSetter(parameterType, "set" + Info.capitalize(names[i]));
				setter.invoke(root, parameters[i]);
			}
			return root;
		} catch (Exception e) {
			throw new VRaptorException(e);
		}
	}

	private Method findSetter(Class<?> parameterType, String methodName) {
		for (Method m : parameterType.getDeclaredMethods()) {
			if (m.getName().equals(methodName)) {
				return m;
			}
		}
		throw new VRaptorException(
				"Unable to instanciate parameters as setter method for parameter setting was not created. "
						+ "Thats probably a bug on your type creator. "
						+ "If you are using the default type creator, notify VRaptor.");
	}
}

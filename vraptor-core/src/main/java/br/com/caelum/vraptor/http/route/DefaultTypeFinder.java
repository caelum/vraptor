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
package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
/**
 * Discover parameter types
 * @author Lucas Cavalcanti
 *
 */
@ApplicationScoped
public class DefaultTypeFinder implements TypeFinder {

	private final ParameterNameProvider provider;
	public DefaultTypeFinder(ParameterNameProvider provider) {
		this.provider = provider;
	}
	public Map<String, Class<?>> getParameterTypes(Method method, String[] parameterPaths) {
		Map<String,Class<?>> result = new HashMap<String, Class<?>>();
		String[] parameterNamesFor = provider.parameterNamesFor(method);
		for (String path : parameterPaths) {
			for (int i = 0; i < parameterNamesFor.length; i++) {
				String name = parameterNamesFor[i];
				if (path.startsWith(name + ".") || path.equals(name)) {
					String[] items = path.split("\\.");
					Class<?> type = method.getParameterTypes()[i];
					for (int j = 1; j < items.length; j++) {
						String item = items[j];
						try {
							type = new Mirror().on(type).reflect().method("get" + upperFirst(item)).withoutArgs().getReturnType();
						} catch (Exception e) {
							throw new IllegalArgumentException("Parameters paths are invalid: " + Arrays.toString(parameterPaths) + " for method " + method, e);
						}
					}
					result.put(path, type);
				}
			}
		}
		return result;
	}
	private String upperFirst(String item) {
		return item.substring(0, 1).toUpperCase() + item.substring(1);
	}

}

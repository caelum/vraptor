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

package br.com.caelum.vraptor.http.ognl;

import static com.google.common.base.Predicates.containsPattern;
import static com.google.common.collect.Maps.filterKeys;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.http.InvalidParameterException;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationMessage;

import com.google.common.base.Defaults;

/**
 * Provides parameters using ognl to parse expression values into parameter
 * values.
 *
 * @author guilherme silveira
 */
@RequestScoped
public class OgnlParametersProvider implements ParametersProvider {

	private final ParameterNameProvider provider;

	private static final Logger logger = LoggerFactory.getLogger(OgnlParametersProvider.class);

	private final HttpServletRequest request;

	private final Container container;

	private final OgnlFacade ognl;

	public OgnlParametersProvider(ParameterNameProvider provider,
			HttpServletRequest request, Container container, OgnlFacade ognl) {
		this.provider = provider;
		this.request = request;
		this.container = container;
		this.ognl = ognl;
	}

	public Object[] getParametersFor(ResourceMethod method, List<Message> errors, ResourceBundle bundle) {

		String[] names = provider.parameterNamesFor(method.getMethod());
		Type[] types = method.getMethod().getGenericParameterTypes();
		Class[] classes = method.getMethod().getParameterTypes();
		Object[] result = new Object[types.length];
		for (int i = 0; i < types.length; i++) {
			Map<String, String[]> requestNames = parametersThatStartWith(names[i]);
			result[i] = createParameter(new Parameter(types[i], classes[i], names[i], method), requestNames, bundle, errors);
		}

		return result;

	}

	private static class Parameter {
		public Type type;
		public Class clazz;
		public String name;
		public ResourceMethod method;

		public Parameter(Type type, Class clazz, String name, ResourceMethod method) {
			this.type = type;
			this.clazz = clazz;
			this.name = name;
			this.method = method;
		}
		public Class actualType() {
			if (type instanceof TypeVariable) {
				ParameterizedType superclass = (ParameterizedType) method.getResource().getType().getGenericSuperclass();
				return (Class) superclass.getActualTypeArguments()[0];
			}
			return clazz;
		}
	}

	private Object createParameter(Parameter param, Map<String, String[]> requestNames, ResourceBundle bundle, List<Message> errors) {
		Object root;
		if (request.getAttribute(param.name) != null) {
			return request.getAttribute(param.name);
		} else if (param.clazz.isInterface() && container.canProvide(param.clazz)) {
            return container.instanceFor(param.clazz);
        } else if (requestNames.isEmpty()) {
			return Defaults.defaultValue(param.actualType());
		} else {
			root = createRoot(param, requestNames, bundle, errors);
			if (root == null) {
				return null;
			}
		}

		ognl.startContext(param.name, param.type, root, bundle);

		for (Entry<String, String[]> parameter : requestNames.entrySet()) {
			String key = parameter.getKey().replaceFirst("^" + param.name + "\\.?", "");
			String[] values = parameter.getValue();
			setProperty(param.name, key, values, errors);
		}

		return ognl.get(param.name);
	}

	private Object createRoot(Parameter param, Map<String, String[]> requestNames, ResourceBundle bundle,
			List<Message> errors) {
		if (requestNames.containsKey(param.name)) {
			String[] values = requestNames.get(param.name);
			try {
				return createSimpleParameter(param, values, bundle);
			} catch(ConversionError ex) {
				errors.add(new ValidationMessage(ex.getMessage(), param.name));
				return null;
			}
		}

		try {
			return ognl.nullHandler().instantiate(param.actualType());
		} catch (Exception ex) {
			throw new InvalidParameterException("unable to instantiate type " + param.type, ex);
		}
	}

	private void setProperty(String name, String key, String[] values, List<Message> errors) {
		try {
			logger.debug("Applying {} with {}",key, values);
			ognl.setValue(name, key, values);
		} catch (ConversionError ex) {
			errors.add(new ValidationMessage(ex.getMessage(), key));
		}
	}

	private Object createSimpleParameter(Parameter param, String[] values, ResourceBundle bundle) {
		if (param.actualType().isArray()) {
			return createArray(param.actualType(), values, bundle);
		}
		if (List.class.isAssignableFrom(param.actualType())) {
			return createList(param.type, bundle, values);
		}
		return convert(param.actualType(), values[0], bundle);
	}

	private Object convert(Class clazz, String value, ResourceBundle bundle) {
		return ognl.createAdapter(bundle).convert(value, clazz);
	}

	private List createList(Type type, ResourceBundle bundle, String[] values) {
		List list = new ArrayList();
		Class actual = getActualType(type);
		for (String value : values) {
			list.add(convert(actual, value, bundle));
		}
		return list;
	}

	private Object createArray(Class clazz, String[] values, ResourceBundle bundle) {
		Class arrayType = clazz.getComponentType();
		Object array = Array.newInstance(arrayType, values.length);
		for (int i = 0; i < values.length; i++) {
			Array.set(array, i, convert(arrayType, values[i], bundle));
		}
		return array;
	}

	private Class getActualType(Type type) {
		return (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
	}

	private Map<String, String[]> parametersThatStartWith(String name) {
		Map<String, String[]> requestNames = filterKeys(request.getParameterMap(), containsPattern("^" + name));
		return new TreeMap<String, String[]>(requestNames);
	}
}

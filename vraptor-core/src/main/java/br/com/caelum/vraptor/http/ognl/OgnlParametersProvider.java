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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import ognl.MethodFailedException;
import ognl.NoSuchPropertyException;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.InvalidParameterException;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.validator.annotation.ValidationException;

/**
 * Provides parameters using ognl to parse expression values into parameter
 * values.
 *
 * @author guilherme silveira
 */
@RequestScoped
public class OgnlParametersProvider implements ParametersProvider {

	private final Container container;

	private final Converters converters;

	private final ParameterNameProvider provider;

	private static final Logger logger = LoggerFactory.getLogger(OgnlParametersProvider.class);

	private final HttpServletRequest request;

	private final EmptyElementsRemoval removal;

	public OgnlParametersProvider(Container container, Converters converters,
			ParameterNameProvider provider, HttpServletRequest request, EmptyElementsRemoval removal) {
		this.container = container;
		this.converters = converters;
		this.provider = provider;
		this.request = request;
		this.removal = removal;
		OgnlRuntime.setNullHandler(Object.class, new ReflectionBasedNullHandler());
		OgnlRuntime.setPropertyAccessor(List.class, new ListAccessor());
		OgnlRuntime.setPropertyAccessor(Object[].class, new ArrayAccessor());
	}

	public Object[] getParametersFor(ResourceMethod method, List<Message> errors, ResourceBundle bundle) {

		String[] names = provider.parameterNamesFor(method.getMethod());
		Type[] types = method.getMethod().getGenericParameterTypes();
		Object[] result = new Object[types.length];
		for (int i = 0; i < types.length; i++) {
			Map<String, String[]> requestNames = filterKeys(request.getParameterMap(), containsPattern("^" + names[i]));
			result[i] = createParameter(types[i], names[i], requestNames, bundle, errors);
		}
		removal.removeExtraElements();

		return result;

	}



	private Object createParameter(Type type, String name, Map<String, String[]> requestNames, ResourceBundle bundle, List<Message> errors) {

		if (requestNames.containsKey(name)) {
			Class clazz = getRawType(type);
			String[] values = requestNames.get(name);
			if (clazz.isArray()) {
				Class arrayType = clazz.getComponentType();
				Object array = Array.newInstance(arrayType, values.length);
				for (int i = 0; i < values.length; i++) {
					Array.set(array, i, converters.to(arrayType).convert(values[i], arrayType, bundle));
				}
				return array;
			}
			if (List.class.isAssignableFrom(clazz)) {
				List list = new ArrayList();
				Class actual = getActualType(type);
				for (String value : values) {
					list.add(converters.to(actual).convert(value, actual, bundle));
				}
				return list;
			}
			return converters.to(clazz).convert(values[0], getRawType(type), bundle);
		}

		OgnlContext context;
		try {
			context = (OgnlContext) Ognl.createDefaultContext(new GenericNullHandler().instantiate(getRawType(type), container));
		} catch (Exception ex) {
			throw new InvalidParameterException("unable to instantiate type " + type, ex);
		}

		context.setTraceEvaluations(true);
		context.put(Container.class, this.container);
		if (List.class.isAssignableFrom(getRawType(type))) {
			context.put("rootType", type);
		}

		VRaptorConvertersAdapter adapter = new VRaptorConvertersAdapter(converters, bundle);
		Ognl.setTypeConverter(context, adapter);


		for (Entry<String, String[]> parameter : requestNames.entrySet()) {
			String key = parameter.getKey().replaceFirst("^" + name + "\\.?", "");
			String[] values = parameter.getValue();

			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Applying " + key + " with " + Arrays.toString(values));
				}
				Ognl.setValue(key, context, context.getRoot(), values.length == 1 ? values[0] : values);
			} catch (ConversionError ex) {
				errors.add(new ValidationMessage(ex.getMessage(), key));
			} catch (MethodFailedException e) { // setter threw an exception

				Throwable cause = e.getCause();
				if (cause.getClass().isAnnotationPresent(ValidationException.class)) {
					errors.add(new ValidationMessage(cause.getLocalizedMessage(), key));
				} else {
					throw new InvalidParameterException("unable to parse expression '" + key + "'", e);
				}

			} catch (NoSuchPropertyException ex) {
				// TODO optimization: be able to ignore or not
				if (logger.isDebugEnabled()) {
					logger.debug("cant find property for expression {} ignoring", key);
				}
				if (logger.isTraceEnabled()) {
					logger.trace("cant find property for expression " + key + ", ignoring. Reason:", ex);

				}
			} catch (OgnlException e) {
				// TODO it fails when parameter name is not a valid java
				// identifier... ignoring by now
				if (logger.isDebugEnabled()) {
					logger.debug("unable to parse expression '" + key + "'. Ignoring", e);
				}
			}
		}
		if (getRawType(type).isArray()) {
			return removal.removeNullsFromArray(context.getRoot());
		}
		return context.getRoot();
	}

	private Class getActualType(Type type) {
		return (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
	}

	private Class getRawType(Type type) {
		if (type instanceof ParameterizedType) {
			return (Class) ((ParameterizedType) type).getRawType();
		}
		return (Class) type;
	}

//	public static void main(String[] args) throws OgnlException {
//
//		OgnlRuntime.setNullHandler(Object.class, new ReflectionBasedNullHandler());
//		OgnlRuntime.setPropertyAccessor(List.class, new ListAccessor());
//
//		List<ABC> list = new ArrayList<ABC>();
//		OgnlContext context = (OgnlContext) Ognl.createDefaultContext(list);
//		context.put("rootType", Types.newParameterizedType(List.class, ABC.class));
//		context.put(Container.class, new Container() {
//
//			public <T> boolean canProvide(Class<T> type) {
//				return false;
//			}
//
//			public <T> T instanceFor(Class<T> type) {
//				return (T) new EmptyElementsRemoval();
//			}
//
//		});
//
//		Ognl.setValue("[2].x", context, list, "222");
////		Object object = Ognl.getValue("new java.util.List()", context);
//
//		System.out.println(list.get(2).x);
////		OgnlRuntime.setPropertyAccessor(Object[].class, new ArrayAccessor());
////		Integer[] ints = new Integer[0];
////		Map context = Ognl.createDefaultContext(ints);
////
////		Ognl.setValue("[1]", context, ints, 23);
////
////		System.out.println(Arrays.toString(ints));
//	}
}

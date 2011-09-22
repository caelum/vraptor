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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import ognl.Evaluation;
import ognl.ListPropertyAccessor;
import ognl.OgnlContext;
import ognl.OgnlException;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.util.StringUtils;

/**
 * This list accessor is responsible for setting null values up to the list
 * size.<br>
 * Compatibility issues might arrive (in previous vraptor versions, the object
 * was instantiated instead of null being set).
 *
 * @author Guilherme Silveira
 *
 */
public class ListAccessor extends ListPropertyAccessor {

	private final Converters converters;

	public ListAccessor(Converters converters) {
		this.converters = converters;
	}

	@Override
	public Object getProperty(Map context, Object target, Object value) throws OgnlException {
		try {
			return super.getProperty(context, target, value);
		} catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setProperty(Map context, Object target, Object key, Object value) throws OgnlException {
		// code comments by Guilherme Silveira in a moment of rage agains ognl
		// code
		List<?> list = (List<?>) target;
		int index = (Integer) key;
		for (int i = list.size(); i <= index; i++) {
			list.add(null);
		}
		if (value instanceof String) {
			// it might be that suckable ognl did not call convert, i.e.: on the
			// values[i] = 2l in a List<Long>.
			// we all just looooove ognl.
			OgnlContext ctx = (OgnlContext) context;
			// if direct injecting, cannot find out what to do, use string

			Type genericType = extractGenericType(ctx, target);

            Class type = getActualType(genericType);

			// suckable ognl doesnt support dependency injection or
			// anything alike... just that suckable context... therefore
			// procedural
			// programming and ognl live together forever!
			Converter<?> converter = converters.to(type);

			ResourceBundle bundle = (ResourceBundle) context.get(ResourceBundle.class);

			Object result = converter.convert((String) value, type, bundle);

			super.setProperty(context, target, key, result);
			return;
		}
		super.setProperty(context, target, key, value);
	}

	private Class getActualType(Type genericType) {
		Class type;
		if (genericType instanceof ParameterizedType) {
		    type = (Class) ((ParameterizedType) genericType).getActualTypeArguments()[0];
		} else {
		    type = (Class) genericType;
		}
		return type;
	}

	private Type extractGenericType(OgnlContext ctx, Object target) {
		Type genericType;

		if (ctx.getRoot() != target) {
			Evaluation eval = ctx.getCurrentEvaluation();
			Evaluation previous = eval.getPrevious();
			String fieldName = previous.getNode().toString();
			Object origin = previous.getSource();
			
			Proxifier proxifier = (Proxifier) ctx.get("proxifier");
			Method getter = new ReflectionBasedNullHandler(proxifier).findGetter(origin, StringUtils.capitalize(fieldName));
			
			genericType = getter.getGenericReturnType();
		} else {
			genericType = (Type) ctx.get("rootType");
		}
		return genericType;
	}

}

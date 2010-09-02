/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource - guilherme.silveira@caelum.com.br
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
package br.com.caelum.vraptor.util.extjs;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.ioc.Component;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

/**
 * Implements the interface ExtJSJson for json serialization in ExtJS standard
 *
 * @author Daniel Kist
 */
@Component
public class ExtJSJsonImpl implements ExtJSJson {

	private final Multimap<Class<?>, String> excludes = LinkedListMultimap.create();
	private final XStream xstream;
	private final HttpServletResponse response;
	private final String SUCCESS  = "{\"success\": ";
	private final String SELECTED = ",\n \"selected\": @value }";

	private Set<Class<?>> elementTypes;
	private Class<?> rootClass;

	private Object object;
	private String json;

	private boolean includeSuccess  = false;
	private boolean successValue;

	private boolean includeSelected = false;
	private Object  selectedValue;


	public ExtJSJsonImpl(HttpServletResponse response) {
		this.xstream  = new XStream(new JsonHierarchicalStreamDriver());
		this.response = response;
	}

	public ExtJSJson from(Object object) {
		response.setContentType("application/json; charset=ISO-8859-1");
		this.rootClass = object.getClass();
		this.object    = object;
		return this;
	}

	public ExtJSJson success() {
		return success(true);
	}

	public ExtJSJson success(boolean success) {
		includeSuccess = true;
		successValue   = success;
		return this;
	}

	public ExtJSJson selected(Object value) {
		if(value != null) {
			includeSelected = true;
			selectedValue = value;
		}
		return this;
	}

	public ExtJSJson exclude(String... names) {
		for (String name : names) {
			Set<Class<?>> parentTypes = getParentTypesFor(name);
			for (Class<?> type : parentTypes) {
				xstream.omitField(type, getNameFor(name));
			}
		}
		return this;
	}

	public ExtJSJson include(String... fields) {
		for (String field : fields) {
			try {
				Set<Class<?>> parentTypes = getParentTypesFor(field);
				String fieldName = getNameFor(field);
				for (Class<?> parentType : parentTypes) {
					Type genericType = new Mirror().on(parentType).reflect().field(fieldName).getGenericType();
					Class<?> fieldType = getActualType(genericType);

					if (!excludes.containsKey(fieldType)) {
						excludeNonPrimitiveFields(fieldType);
					}
					excludes.remove(parentType, fieldName);
				}
			} catch (NullPointerException e) {
				throw new IllegalArgumentException("Field path " + field + " doesn't exist");
			}
		}
		return this;
	}

	public ExtJSJson serialize() {
		this.json = objectToJSON();
		if(includeSelected) {
			includeSelected();
		}

		if(includeSuccess) {
			includeSuccess();
		}

		try {
			response.getWriter().write(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	private void excludeNonPrimitiveFields(Class<?> type) {
		for (Field field : new Mirror().on(type).reflectAll().fields()) {
			if (!isPrimitive(field.getType())) {
				excludes.put(type, field.getName());
			}
		}
	}

	private String objectToJSON() {
		xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias("data", object.getClass());

        for(Class clazz : object.getClass().getInterfaces()) {
        	xstream.alias("data", clazz);
        }

        String json = xstream.toXML(object).trim();
        if(json.endsWith("}}")) {
        	json = json.substring(0, json.length() - 2) + " }\n}";
        }
        return json;
	}

	private String getNameFor(String name) {
		String[] path = name.split("\\.");
		return path[path.length-1];
	}

	private Set<Class<?>> getParentTypesFor(String name) {
		if (elementTypes == null) {
			Class<?> type = rootClass;
			return Collections.<Class<?>>singleton(getParentType(name, type));
		} else {
			Set<Class<?>> result = new HashSet<Class<?>>();
			for (Class<?> type : elementTypes) {
				result.add(getParentType(name, type));
			}
			return result;
		}
	}

	private Class<?> getParentType(String name, Class<?> type) {
		String[] path = name.split("\\.");
		for (int i = 0; i < path.length - 1; i++) {
			type = getActualType(new Mirror().on(type).reflect().field(path[i]).getGenericType());
		}
		return type;
	}

	private Class<?> getActualType(Type genericType) {
		if (genericType instanceof ParameterizedType) {
			ParameterizedType type = (ParameterizedType) genericType;
			if (isCollection(type)) {
				return (Class<?>) type.getActualTypeArguments()[0];
			}
		}
		return (Class<?>) genericType;
	}

	private boolean isCollection(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType ptype = (ParameterizedType) type;
			return Collection.class.isAssignableFrom((Class<?>) ptype.getRawType())
			  || Map.class.isAssignableFrom((Class<?>) ptype.getRawType());
		}
		return Collection.class.isAssignableFrom((Class<?>) type);
	}

	private void includeSuccess() {
		if(json != null && json.length() > 0 && json.startsWith("{") && json.endsWith("}")) {
			this.json = SUCCESS + successValue + ", \n " + this.json.substring(1);
		}
	}

	private void includeSelected() {
		if(json != null && json.length() > 0 && json.startsWith("{") && json.endsWith("}")) {
			String v = null;
			if(isNumeric(selectedValue.getClass())) {
				v = selectedValue.toString();
			} else {
				v = "\"" + selectedValue.toString() + "\"";
			}
			this.json = this.json.substring(0, json.length() - 1) + SELECTED.replace("@value", v);
		}
	}

	private boolean isPrimitive(Class<?> type) {
		return type.isPrimitive()
			|| type.isEnum()
			|| Number.class.isAssignableFrom(type)
			|| type.equals(String.class)
			|| Date.class.isAssignableFrom(type)
			|| Calendar.class.isAssignableFrom(type)
			|| Boolean.class.equals(type)
			|| Character.class.equals(type);
	}

	private boolean isNumeric(Class clazz) {
		return Integer.class.equals(clazz)
			|| Double.class.equals(clazz)
			|| Long.class.equals(clazz)
			|| Number.class.equals(clazz);
	}
}

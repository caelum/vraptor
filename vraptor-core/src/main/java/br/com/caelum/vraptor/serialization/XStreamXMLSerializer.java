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
package br.com.caelum.vraptor.serialization;

import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map.Entry;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.thoughtworks.xstream.XStream;

/**
 * A Xml Serializer based on XStream
 * @author Lucas Cavalcanti
 * @since 3.0.2
 */
public class XStreamXMLSerializer implements Serializer {

	private final XStream xstream;
	private final Writer writer;
	private Object toSerialize;
	private final TypeNameExtractor extractor;
	private final Multimap<Class<?>, String> excludes = LinkedListMultimap.create();

	public XStreamXMLSerializer(XStream xstream, Writer writer, TypeNameExtractor extractor) {
		this.xstream = xstream;
		this.writer = writer;
		this.extractor = extractor;
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

	public Serializer exclude(String... names) {
		for (String name : names) {
			xstream.omitField(getParentTypeFor(name), getNameFor(name));
		}
		return this;
	}

	private String getNameFor(String name) {
		String[] path = name.split("\\.");
		return path[path.length-1];
	}

	private Class<?> getParentTypeFor(String name) {
		Class<?> type = toSerialize.getClass();
		String[] path = name.split("\\.");
		for (int i = 0; i < path.length - 1; i++) {
			type = getActualType(new Mirror().on(type).reflect().field(path[i]).getGenericType());
		}
		return type;
	}

	public <T> Serializer from(T object, String alias) {
		if (object == null) {
			throw new NullPointerException("You can't serialize null objects");
		}
		if (Collection.class.isInstance(object)) {
			throw new IllegalArgumentException("It's not possible to serialize colections yet. " +
				"Create a class that wraps this collections by now.");
		} else {
			Class<?> type = object.getClass();
			xstream.alias(alias, type);
			excludeNonPrimitiveFields(type);
		}
		this.toSerialize = object;
		return this;
	}

	public <T> Serializer from(T object) {
		if (object == null) {
			throw new NullPointerException("You can't serialize null objects");
		}
		return from(object, extractor.nameFor(object.getClass()));
	}

	private void excludeNonPrimitiveFields(Class<?> type) {
		for (Field field : new Mirror().on(type).reflectAll().fields()) {
			if (!isPrimitive(field.getType())) {
				excludes.put(type, field.getName());
			}
		}
	}

	public Serializer include(String... fields) {
		for (String field : fields) {
			Class<?> parentType = getParentTypeFor(field);
			String fieldName = getNameFor(field);
			Type genericType = new Mirror().on(parentType).reflect().field(fieldName).getGenericType();
			Class<?> fieldType = getActualType(genericType);
			if (isCollection(genericType)) {
				xstream.alias(extractor.nameFor(fieldType), fieldType);
			}
			if (!excludes.containsKey(fieldType)) {
				excludeNonPrimitiveFields(fieldType);
			}
			excludes.remove(parentType, fieldName);
		}
		return this;
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
			return Collection.class.isAssignableFrom((Class<?>) ptype.getRawType());
		}
		return Collection.class.isAssignableFrom((Class<?>) type);
	}

	public void serialize() {
		for (Entry<Class<?>, String> exclude : excludes.entries()) {
			xstream.omitField(exclude.getKey(), exclude.getValue());
		}
		xstream.toXML(toSerialize, writer);
	}


}

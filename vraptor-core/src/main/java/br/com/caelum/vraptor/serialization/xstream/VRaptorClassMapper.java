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

package br.com.caelum.vraptor.serialization.xstream;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.validator.Message;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class VRaptorClassMapper extends MapperWrapper {

	private final Supplier<TypeNameExtractor> extractor;
	private Serializee serializee;
	
	public VRaptorClassMapper(Mapper wrapped, Supplier<TypeNameExtractor> supplier) {
		super(wrapped);
		this.extractor = supplier;
	}
	
	static boolean isPrimitive(Class<?> type) {
		return type.isPrimitive()
			|| type.isEnum()
			|| Number.class.isAssignableFrom(type)
			|| type.equals(String.class)
			|| Date.class.isAssignableFrom(type)
			|| Calendar.class.isAssignableFrom(type)
			|| Boolean.class.equals(type)
			|| Character.class.equals(type);
	}
	
	@Override
	public boolean shouldSerializeMember(Class definedIn, String fieldName) {
		for (String include : serializee.getIncludes()) {
			if (isCompatiblePath(include, definedIn, fieldName)) {
				return true;
			}
		}
		for (String exclude : serializee.getExcludes()) {
			if (isCompatiblePath(exclude, definedIn, fieldName)) {
				return false;
			}
		}
		
		boolean should = super.shouldSerializeMember(definedIn, fieldName); 
		if (!serializee.isRecursive())
			should = should && isPrimitive(new Mirror().on(definedIn).reflect().field(fieldName).getType()); 
		return should;
	}

	private boolean isCompatiblePath(String path, Class definedIn, String fieldName) {
		return (path.equals(fieldName) || path.endsWith("." + fieldName)) && getParentTypesFor(serializee, path).contains(definedIn);
	}

	@Override
	public String serializedClass(Class type) {
		if (Message.class.isAssignableFrom(type)) {
			return "message";
		}
		String superName = super.serializedClass(type);
		if (type.getName().equals(superName)) {
			return extractor.get().nameFor(type);
		}
		return superName;
	}
	
	static Class<?> getActualType(Type genericType) {
		if (genericType instanceof ParameterizedType) {
			ParameterizedType type = (ParameterizedType) genericType;

			if (isCollection(type)) {
				Type actualType = type.getActualTypeArguments()[0];

				if (actualType instanceof TypeVariable<?>) {
					return (Class<?>) type.getRawType();
				}

				return (Class<?>) actualType;
			}
		}

		return (Class<?>) genericType;
	}

	static boolean isCollection(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType ptype = (ParameterizedType) type;
			return Collection.class.isAssignableFrom((Class<?>) ptype.getRawType())
			  || Map.class.isAssignableFrom((Class<?>) ptype.getRawType());
		}
		return Collection.class.isAssignableFrom((Class<?>) type);
	}

	
	static Set<Class<?>> getParentTypesFor(Serializee serializee, String name) {
		if (serializee.getElementTypes() == null) {
			Class<?> type = serializee.getRootClass();
			return getParentTypes(name, type);
		} else {
			Set<Class<?>> result = new HashSet<Class<?>>();
			for (Class<?> type : serializee.getElementTypes()) {
				result.addAll(getParentTypes(name, type));
			}
			return result;
		}
	}

	static Set<Class<?>> getParentTypes(String name, Class<?> type) {
		String[] path = name.split("\\.");
		for (int i = 0; i < path.length - 1; i++) {
			type = getActualType(new Mirror().on(type).reflect().field(path[i]).getGenericType());
		}
		Set<Class<?>> types = Sets.newHashSet();
		while(type != Object.class) {
			types.add(type);
			type = type.getSuperclass();
		}
		return types;
	}

	public void setSerializee(Serializee serializee) {
		this.serializee = serializee;
	}


}

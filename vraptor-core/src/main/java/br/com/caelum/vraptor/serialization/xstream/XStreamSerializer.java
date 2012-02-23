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
package br.com.caelum.vraptor.serialization.xstream;

import static br.com.caelum.vraptor.serialization.xstream.VRaptorClassMapper.isPrimitive;
import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.ProxyInitializer;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.thoughtworks.xstream.XStream;

/**
 * A SerializerBuilder based on XStream
 * @author Lucas Cavalcanti
 * @since 3.0.2
 */
public class XStreamSerializer implements SerializerBuilder {

	private final XStream xstream;
	private final Writer writer;
	private final TypeNameExtractor extractor;
	private final ProxyInitializer initializer;
	private final Serializee serializee = new Serializee();

	public XStreamSerializer(XStream xstream, Writer writer, TypeNameExtractor extractor, ProxyInitializer initializer) {
		this.xstream = xstream;
		this.writer = writer;
		this.extractor = extractor;
		this.initializer = initializer;
	}

	public Serializer exclude(String... names) {
		serializee.excludeAll(names);
		return this;
	}


	private void preConfigure(Object obj,String alias) {
		checkNotNull(obj, "You can't serialize null objects");

		xstream.processAnnotations(obj.getClass());

		serializee.setRootClass(initializer.getActualClass(obj));
		if (alias == null && initializer.isProxy(obj.getClass())) {
			alias = extractor.nameFor(serializee.getRootClass());
		}

		setRoot(obj);

		setAlias(obj, alias);
	}

	private void setRoot(Object obj) {
		if (Collection.class.isInstance(obj)) {
			this.serializee.setRoot(normalizeList(obj));
		} else {
			this.serializee.setRoot(obj);
		}
	}

	private Collection<Object> normalizeList(Object obj) {
		Collection<Object> list;
		if (hasDefaultConverter()) {
			list = new ArrayList<Object>((Collection<?>)obj);
		} else {
			list = (Collection<Object>) obj;
		}
		serializee.setElementTypes(findElementTypes(list));
		return list;
	}

	private boolean hasDefaultConverter() {
		return xstream.getConverterLookup().lookupConverterForType(serializee.getRootClass()).equals(xstream.getConverterLookup().lookupConverterForType(Object.class));
	}

	private void setAlias(Object obj, String alias) {
		if (alias != null) {
			if (Collection.class.isInstance(obj) && (List.class.isInstance(obj) || hasDefaultConverter())) {
				xstream.alias(alias, List.class);
			}
			xstream.alias(alias, obj.getClass());
		}
	}

	public <T> Serializer from(T object, String alias) {
		preConfigure(object, alias);
		return this;
	}

	public <T> Serializer from(T object) {
		preConfigure(object, null);
		return this;
	}

	private Set<Class<?>> findElementTypes(Collection<Object> list) {
		Set<Class<?>> set = new HashSet<Class<?>>();
		for (Object element : list) {
			if (element != null && !isPrimitive(element.getClass())) {
				set.add(initializer.getActualClass(element));
			}
		}
		return set;
	}

	private void excludeNonPrimitiveFields(Multimap<Class<?>, String> excludesMap, Class<?> type) {
		for (Field field : new Mirror().on(type).reflectAll().fields()) {
			if (!isPrimitive(field.getType())) {
				excludesMap.put(field.getDeclaringClass(), field.getName());
			}
		}
	}

	public Serializer include(String... fields) {
		serializee.includeAll(fields);
		return this;
	}

	private void parseInclude(Multimap<Class<?>, String> excludesMap, Entry<String, Class<?>> include) {
		Class<?> parentType = include.getValue();
		String fieldName = getNameFor(include.getKey());
		Type genericType = new Mirror().on(parentType).reflect().field(fieldName).getGenericType();
		Class<?> fieldType = Serializee.getActualType(genericType);

		if (!excludesMap.containsKey(fieldType)) {
			excludeNonPrimitiveFields(excludesMap, fieldType);
		}
		excludesMap.remove(parentType, fieldName);
	}

	public void serialize() {
		if (xstream instanceof VRaptorXStream) {
			VRaptorClassMapper mapper = ((VRaptorXStream) xstream).getVRaptorMapper();
			mapper.setSerializee(serializee);
		} else {
			Multimap<Class<?>, String> excludesMap = LinkedListMultimap.create();
			if (!serializee.isRecursive()) {
				Class<?> type = serializee.getRootClass();
				excludeNonPrimitiveFields(excludesMap, type);
				
				for (Class<?> eType : firstNonNull(serializee.getElementTypes(), Collections.<Class<?>>emptySet())) {
					excludeNonPrimitiveFields(excludesMap, eType);
				}
			}
			for (Entry<String, Class<?>> exclude : serializee.getExcludes().entries()) {
				parseExclude(exclude);
			}
			for (Entry<String, Class<?>> include : serializee.getIncludes().entries()) {
				parseInclude(excludesMap, include);
			}
			
			for (Entry<Class<?>, String> exclude : excludesMap.entries()) {
				xstream.omitField(exclude.getKey(), exclude.getValue());
			}
		}
		
		registerProxyInitializer();
		xstream.toXML(serializee.getRoot(), writer);
	}

	public Serializer recursive() {
		this.serializee.setRecursive(true);
		return this;
	}

	private void registerProxyInitializer() {
		xstream.registerConverter(new ProxyConverter(initializer, xstream));
	}
	
	private void parseExclude(Entry<String, Class<?>> exclude) {
		xstream.omitField(exclude.getValue(), getNameFor(exclude.getKey()));
	}

	private String getNameFor(String name) {
		String[] path = name.split("\\.");
		return path[path.length-1];
	}

}

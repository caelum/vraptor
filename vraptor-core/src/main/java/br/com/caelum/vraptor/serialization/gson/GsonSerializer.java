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
package br.com.caelum.vraptor.serialization.gson;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.ProxyInitializer;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.serialization.xstream.Serializee;

import com.google.gson.Gson;

/**
 * A SerializerBuilder based on Gson
 * 
 * @author Renan Reis
 * @author Guilherme Mangabeira
 */

public class GsonSerializer implements SerializerBuilder {

	private final Writer writer;

	private final TypeNameExtractor extractor;

	private final ProxyInitializer initializer;

	private final Serializee serializee;

	protected VRaptorGsonBuilder builder;

	public GsonSerializer(VRaptorGsonBuilder builder, Writer writer, TypeNameExtractor extractor,
			ProxyInitializer initializer, Serializee serializee) {
		this.writer = writer;
		this.extractor = extractor;
		this.initializer = initializer;
		this.builder = builder;
		this.serializee = serializee;
	}

	public Serializer exclude(String... names) {
		serializee.excludeAll(names);
		return this;
	}

	public Serializer excludeAll() {
		serializee.excludeAll();
		return this;
	}

	private void preConfigure(Object obj, String alias) {
		checkNotNull(obj, "You can't serialize null objects");

		serializee.setRootClass(initializer.getActualClass(obj));

		if (alias == null) {
			if (Collection.class.isInstance(obj) && (List.class.isInstance(obj))) {
				alias = "list";
			} else {
				alias = extractor.nameFor(serializee.getRootClass());
			}
		}

		builder.setAlias(alias);

		setRoot(obj);
	}

	private void setRoot(Object obj) {
		if (Collection.class.isInstance(obj)) {
			this.serializee.setRoot(normalizeList(obj));
		} else {
			this.serializee.setRoot(obj);
		}
	}

	@SuppressWarnings("unchecked")
	private Collection<Object> normalizeList(Object obj) {
		Collection<Object> list;
		list = (Collection<Object>) obj;
		serializee.setElementTypes(findElementTypes(list));

		return list;
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

	public Serializer include(String... fields) {
		serializee.includeAll(fields);
		return this;
	}

	public void serialize() {
		try {
			Object root = serializee.getRoot();

			builder.setExclusionStrategies(new Exclusions(serializee));

			Gson gson = builder.create();

			String alias = builder.getAlias();
			if (builder.isWithoutRoot()) {
				writer.write(gson.toJson(root));
			} else {
				Map<String, Object> tree = new HashMap<String, Object>();
				tree.put(alias, root);
				writer.write(gson.toJson(tree));
			}

			writer.flush();
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException("Não pode serializar", e);
		}
	}

	public Serializer recursive() {
		this.serializee.setRecursive(true);
		return this;
	}

	static boolean isPrimitive(Class<?> type) {
		return type.isPrimitive() || type.isEnum() || Number.class.isAssignableFrom(type) || type.equals(String.class)
				|| Date.class.isAssignableFrom(type) || Calendar.class.isAssignableFrom(type)
				|| Boolean.class.equals(type) || Character.class.equals(type);
	}
}
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

package br.com.caelum.vraptor.restfulie.serialization;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.util.StringUtils;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class MethodValueSupportConverter implements Converter {

	private final ReflectionConverter delegate;

	public MethodValueSupportConverter(ReflectionConverter delegate) {
		this.delegate = delegate;
	}

	public boolean canConvert(Class type) {
		return delegate.canConvert(type);
	}

	public void marshal(Object root, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		for (Method m : root.getClass().getMethods()) {
			if (m.isAnnotationPresent(XStreamSerialize.class)) {
				writeTag(root, m, writer, context);
			}
		}
		delegate.marshal(root, writer, context);
	}

	private void writeTag(Object root, Method m, HierarchicalStreamWriter writer, MarshallingContext context) {
		writer.startNode(nameFor(m));
		try {
			context.convertAnother(m.invoke(root));
		} catch (Exception e) {
			throw new XStreamException(e.getMessage(), e);
		}
		writer.endNode();
	}

	private String nameFor(Method m) {
		String name = m.getName();
		if (name.startsWith("is")) {
			return StringUtils.lowercaseFirst(name.substring(2));
		} else if (name.startsWith("get")) {
			return StringUtils.lowercaseFirst(name.substring(3));
		}
		return name;
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		return delegate.unmarshal(reader, context);
	}

}

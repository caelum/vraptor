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
package br.com.caelum.vraptor.validator;

import br.com.caelum.vraptor.ioc.Component;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
/**
 * Converter for normalizing {@link Message} serialization 
 * @author Lucas Cavalcanti
 * @since 3.4.0
 */
@Component
public class MessageConverter implements Converter {

	public boolean canConvert(Class type) {
		return Message.class.isAssignableFrom(type);
	}

	public void marshal(Object val, HierarchicalStreamWriter writer, MarshallingContext context) {
		Message message = (Message) val;
		startNode("message", writer);
		writer.setValue(message.getMessage());
		writer.endNode();
		
		startNode("category", writer);
		writer.setValue(message.getCategory());
		writer.endNode();
	}

	private void startNode(String name, HierarchicalStreamWriter writer) {
		if (writer instanceof ExtendedHierarchicalStreamWriter) {
			ExtendedHierarchicalStreamWriter extendedWriter = (ExtendedHierarchicalStreamWriter) writer;
			extendedWriter.startNode(name, String.class);
		} else {
			writer.startNode(name);
		}
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		return null;
	}

}

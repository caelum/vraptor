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

package br.com.caelum.vraptor.restfulie.serialization;

import java.io.Writer;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.ProxyInitializer;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.serialization.xstream.XStreamSerializer;

import com.thoughtworks.xstream.XStream;

public class LinksSerializer implements SerializerBuilder {

	private final XStreamSerializer serializer;

	public LinksSerializer(XStream xstream, Writer writer, TypeNameExtractor extractor, ProxyInitializer initializer) {
		this.serializer = new XStreamSerializer(xstream, writer, extractor, initializer);
	}

	public Serializer exclude(String... arg0) {
		return serializer.exclude(arg0);
	}

	public <T> Serializer from(T object) {
		return serializer.from(object);
	}

	public Serializer include(String... arg0) {
		return serializer.include(arg0);
	}

	public void serialize() {
		serializer.serialize();
	}

	public <T> Serializer from(T arg0, String arg1) {
		return serializer.from(arg0, arg1);
	}

	public Serializer recursive() {
		return serializer.recursive();
	}

}

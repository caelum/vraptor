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

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.validator.Message;

import com.google.common.base.Supplier;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class VRaptorClassMapper extends MapperWrapper {

	private final Supplier<TypeNameExtractor> extractor;

	public VRaptorClassMapper(Mapper wrapped, Supplier<TypeNameExtractor> supplier) {
		super(wrapped);
		this.extractor = supplier;
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

}

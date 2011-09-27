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

import static br.com.caelum.vraptor.view.Results.page;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;

/**
 * delegates the serialization for the default view
 * @author Lucas Cavalcanti
 * @since 3.1.3
 */
public class HTMLSerialization implements Serialization {

	private final Result result;
	private final TypeNameExtractor extractor;

	public HTMLSerialization(Result result, TypeNameExtractor extractor) {
		this.result = result;
		this.extractor = extractor;
	}

	public boolean accepts(String format) {
		return "html".equals(format);
	}

	public <T> Serializer from(T object, String alias) {
		result.include(alias, object);
		result.use(page()).defaultView();
		return new IgnoringSerializer();
	}

	public <T> Serializer from(T object) {
		result.include(extractor.nameFor(object.getClass()), object);
		result.use(page()).defaultView();
		return new IgnoringSerializer();
	}

}

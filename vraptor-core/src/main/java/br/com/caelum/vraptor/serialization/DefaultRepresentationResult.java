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

import java.util.List;

import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.view.PageResult;

/**
 * Default implementation for RepresentationResult that uses request Accept format to
 * decide which representation will be used
 * @author Lucas Cavalcanti
 * @author Jose Donizetti
 * @since 3.0.3
 */
public class DefaultRepresentationResult implements RepresentationResult {

	private final FormatResolver formatResolver;
	private List<Serialization> serializations;
	private final PageResult result;

	public DefaultRepresentationResult(FormatResolver formatResolver, PageResult result, List<Serialization> serializations) {
		this.formatResolver = formatResolver;
		this.result = result;
		this.serializations = serializations;
	}

	public <T> Serializer from(T object) {
		String format = formatResolver.getAcceptFormat();
		for (Serialization serialization : serializations) {
			if (serialization.accepts(format)) {
				return serialization.from(object);
			}
		}
		// if content negotiation fails, relies to html
		result.forward();
		return new IgnoringSerializer();
	}
}

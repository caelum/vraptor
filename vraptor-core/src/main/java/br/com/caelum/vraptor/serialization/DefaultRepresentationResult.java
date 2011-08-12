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

import static br.com.caelum.vraptor.view.Results.status;

import java.util.Collections;
import java.util.List;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.restfulie.RestHeadersHandler;
import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;

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
	private final Result result;
	private final RestHeadersHandler headersHandler;

	public DefaultRepresentationResult(FormatResolver formatResolver, Result result, List<Serialization> serializations, RestHeadersHandler headersHandler) {
		this.formatResolver = formatResolver;
		this.result = result;
		this.serializations = serializations;
		this.headersHandler = headersHandler;
	}

	public <T> Serializer from(T object) {
		return from(object, null);
	}

    /**
     * Override this method if you want another ordering strategy.
     *
     * @since 3.4.0
     */
    protected void sortSerializations() {
        Collections.sort(this.serializations, new PackageComparator());
    }

	public <T> Serializer from(T object, String alias) {
		if(object == null) {
			result.use(status()).notFound();
			return new IgnoringSerializer();
		}
		if(HypermediaResource.class.isAssignableFrom(object.getClass())) {
			headersHandler.handle(HypermediaResource.class.cast(object));
		}
        sortSerializations();
		String format = formatResolver.getAcceptFormat();
		for (Serialization serialization : serializations) {
			if (serialization.accepts(format)) {
				if(alias==null) {
					return serialization.from(object);
				} else {
					return serialization.from(object, alias);
				}
			}
		}
		result.use(status()).notAcceptable();

		return new IgnoringSerializer();
	}
}

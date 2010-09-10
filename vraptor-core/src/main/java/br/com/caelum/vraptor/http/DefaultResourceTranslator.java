/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
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

package br.com.caelum.vraptor.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.route.MethodNotAllowedException;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Basic url to resource method translator.
 *
 * @author Guilherme Silveira
 * @author Leonardo Bessa
 */
@ApplicationScoped
public class DefaultResourceTranslator implements UrlToResourceTranslator {

	private final Logger logger = LoggerFactory.getLogger(DefaultResourceTranslator.class);

	private final Router router;

	public DefaultResourceTranslator(Router router) {
		this.router = router;
	}

	public ResourceMethod translate(RequestInfo info) {
		MutableRequest request = info.getRequest();
		String resourceName = info.getRequestedUri();

		logger.debug("trying to access {}", resourceName);

		HttpMethod method;
		try {
			method = HttpMethod.of(request);
		} catch (IllegalArgumentException e) {
			throw new MethodNotAllowedException(router.allowedMethodsFor(resourceName), request.getMethod());
		}
		ResourceMethod resource = router.parse(resourceName, method, request);

		logger.debug("found resource {}", resource);
		return resource;
	}

}

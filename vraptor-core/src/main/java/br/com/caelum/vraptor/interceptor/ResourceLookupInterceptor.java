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

package br.com.caelum.vraptor.interceptor;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.ResourceNotFoundHandler;

/**
 * Looks up the resource for a specific request and delegates for the 404
 * component if unable to find it.
 *
 * @author Guilherme Silveira
 * @author Cecilia Fernandes
 */
public class ResourceLookupInterceptor implements Interceptor {

	private final UrlToResourceTranslator translator;
	private final MethodInfo requestInfo;
	private final RequestInfo request;
	private final ResourceNotFoundHandler resourceNotFoundHandler;

	public ResourceLookupInterceptor(UrlToResourceTranslator translator, MethodInfo requestInfo,
			ResourceNotFoundHandler resourceNotFoundHandler, RequestInfo request) {
		this.translator = translator;
		this.requestInfo = requestInfo;
		this.request = request;
		this.resourceNotFoundHandler = resourceNotFoundHandler;
	}

	public void intercept(InterceptorStack invocation, ResourceMethod ignorableMethod, Object resourceInstance)
			throws InterceptionException {
		ResourceMethod method = translator.translate(request.getRequest());
		if (method == null) {
			resourceNotFoundHandler.couldntFind(request);
			return;
		}
		requestInfo.setResourceMethod(method);
		invocation.next(method, resourceInstance);
	}

	public boolean accepts(ResourceMethod method) {
		return true;
	}

}

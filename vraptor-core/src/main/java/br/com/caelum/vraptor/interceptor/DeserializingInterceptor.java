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
package br.com.caelum.vraptor.interceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.deserialization.Deserializer;
import br.com.caelum.vraptor.deserialization.Deserializers;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.HttpResult;

/**
 * Important: this interceptor must be after the {@link ParametersInstantiatorInterceptor}
 *
 * @author Lucas Cavalcanti
 * @author Rafael Ferreira
 * @since 3.0.2
 */
public class DeserializingInterceptor implements Interceptor {
	private static final int UNSUPPORTED_MEDIA_TYPE = 415;
	private final HttpServletRequest request;
	private final HttpResult result;
	private final Deserializers deserializers;
	private final MethodInfo methodInfo;
	private final Container container;

	public DeserializingInterceptor(HttpServletRequest servletRequest, HttpResult result,
			Deserializers deserializers, MethodInfo methodInfo, Container container) {
		this.request = servletRequest;
		this.result = result;
		this.deserializers = deserializers;
		this.methodInfo = methodInfo;
		this.container = container;
	}

	public boolean accepts(ResourceMethod method) {
		return method.containsAnnotation(Consumes.class);
	}

	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
		Consumes consumesAnnotation = method.getMethod().getAnnotation(Consumes.class);
		List<String> supported =  Arrays.asList(consumesAnnotation.value());

		String contentType = request.getContentType();
		if (!supported.isEmpty() && !supported.contains(contentType)) {
			String errorMessage = String.format("Request with media type [%s]. Expecting one of %s.",
							contentType, supported);
			result.sendError(UNSUPPORTED_MEDIA_TYPE, errorMessage);
			return;
		}

		try {
			Deserializer deserializer = deserializers.deserializerFor(contentType, container);

			Object[] deserialized = deserializer.deserialize(request.getInputStream(), method);
			Object[] parameters = methodInfo.getParameters();

			for (int i = 0; i < deserialized.length; i++) {
				if (deserialized[i] != null) {
					parameters[i] = deserialized[i];
				}
			}

			stack.next(method, resourceInstance);
		} catch (IOException e) {
			throw new InterceptionException(e);
		}

	}

}

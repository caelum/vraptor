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

package br.com.caelum.vraptor.resource;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Default implementation which sets the header and send an error response.
 * @author guilherme
 *
 */
@ApplicationScoped
public class DefaultMethodNotAllowedHandler implements MethodNotAllowedHandler {

	public void deny(RequestInfo request, Set<HttpMethod> allowedMethods) {
		request.getResponse().addHeader(
				"Allow", allowedMethods.toString().replaceAll("\\[|\\]", ""));
		try {
			if (!"OPTIONS".equalsIgnoreCase(request.getRequest().getMethod())) {
				request.getResponse().sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			}
		} catch (IOException e) {
			throw new InterceptionException(e);
		}
	}

}

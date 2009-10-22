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

package br.com.caelum.vraptor.resource;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Default 404 component. It defers the request back to container
 *
 * @author Lucas Cavalcanti
 * @author Luiz Real
 */
@ApplicationScoped
public class DefaultResourceNotFoundHandler implements ResourceNotFoundHandler {

	public void couldntFind(RequestInfo request) {
		FilterChain chain = request.getChain();
		try {
			chain.doFilter(request.getRequest(), request.getResponse());
		} catch (IOException e) {
			throw new InterceptionException(e);
		} catch (ServletException e) {
			throw new InterceptionException(e);
		}
	}

}

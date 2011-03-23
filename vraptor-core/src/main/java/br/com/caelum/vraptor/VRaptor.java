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

package br.com.caelum.vraptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.core.Application;
import br.com.caelum.vraptor.core.DefaultApplication;

/**
 * VRaptor entry point.<br>
 * Provider configuration is supported through init parameter.
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
public class VRaptor implements Filter {
	
	private Application context;

	public void destroy() {
		context.stop();
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		if (!(req instanceof HttpServletRequest) || !(res instanceof HttpServletResponse)) {
			throw new ServletException(
					"VRaptor must be run inside a Servlet environment. Portlets and others aren't supported.");
		}
		context.parse((HttpServletRequest)req, (HttpServletResponse) res, chain);
	}

	public void init(FilterConfig config) throws ServletException {
		context = new DefaultApplication(config.getServletContext());
		context.start();
	}
}

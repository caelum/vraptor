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
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.config.BasicConfiguration;
import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.core.StaticContentHandler;
import br.com.caelum.vraptor.http.EncodingHandler;
import br.com.caelum.vraptor.http.VRaptorRequest;
import br.com.caelum.vraptor.http.VRaptorResponse;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;

/**
 * VRaptor entry point.<br>
 * Provider configuration is supported through init parameter.
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
public class VRaptor implements Filter {
	private ContainerProvider provider;
	private ServletContext servletContext;

	private StaticContentHandler staticHandler;

	private static final Logger logger = LoggerFactory.getLogger(VRaptor.class);

	public void destroy() {
		provider.stop();
		provider = null;
		servletContext = null;
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {

		if (!(req instanceof HttpServletRequest) || !(res instanceof HttpServletResponse)) {
			throw new ServletException(
					"VRaptor must be run inside a Servlet environment. Portlets and others aren't supported.");
		}

		final HttpServletRequest baseRequest = (HttpServletRequest) req;
		final HttpServletResponse baseResponse = (HttpServletResponse) res;

		if (staticHandler.requestingStaticFile(baseRequest)) {
			staticHandler.deferProcessingToContainer(chain, baseRequest, baseResponse);
		} else {
			logger.debug("VRaptor received a new request");
			logger.trace("Request: {}", req);

			VRaptorRequest mutableRequest = new VRaptorRequest(baseRequest);
			VRaptorResponse mutableResponse = new VRaptorResponse(baseResponse);

			final RequestInfo request = new RequestInfo(servletContext, chain, mutableRequest, mutableResponse);
			provider.provideForRequest(request, new Execution<Object>() {
				public Object insideRequest(Container container) {
					container.instanceFor(EncodingHandler.class).setEncoding(baseRequest, baseResponse);
					container.instanceFor(RequestExecution.class).execute();
					return null;
				}
			});
			logger.debug("VRaptor ended the request");
		}
	}

	public void init(FilterConfig cfg) throws ServletException {
		servletContext = cfg.getServletContext();
		BasicConfiguration config = new BasicConfiguration(servletContext);
		init(config.getProvider());
		logger.info("VRaptor 3.5.0-SNAPSHOT successfuly initialized");
	}

	void init(ContainerProvider provider) {
		this.provider = provider;
		this.provider.start(servletContext);
		this.staticHandler = provider.getContainer().instanceFor(StaticContentHandler.class);
	}

}

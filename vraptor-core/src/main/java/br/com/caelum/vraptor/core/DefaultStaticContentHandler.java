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
package br.com.caelum.vraptor.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

/**
 * Handles default content if the request corresponds to static content.
 *
 * @author guilherme silveira
 * @author unknown - based on vraptor2
 */
@Component
@ApplicationScoped
public class DefaultStaticContentHandler implements StaticContentHandler {

	private static final Logger logger = LoggerFactory.getLogger(DefaultStaticContentHandler.class);

	private final ServletContext context;

	public DefaultStaticContentHandler(ServletContext context) {
		this.context = context;
	}

	public boolean requestingStaticFile(HttpServletRequest request) throws MalformedURLException {
		URL resourceUrl = context.getResource(uriRelativeToContextRoot(request));
		return resourceUrl != null && isAFile(resourceUrl);
	}

	private String uriRelativeToContextRoot(HttpServletRequest request) {
		return request.getRequestURI().substring(request.getContextPath().length());
	}

	private boolean isAFile(URL resourceUrl) {
		return !resourceUrl.toString().endsWith("/");
	}

	public void deferProcessingToContainer(FilterChain filterChain, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		logger.debug("Deferring request to container: {} ", request.getRequestURI());
		filterChain.doFilter(request, response);
	}

}

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

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;

/**
 * Simple wrapper for request, response and servlet context.
 *
 * @author Fabio Kung
 * @author Guilherme Silveira
 */
public class RequestInfo {
	private final ServletContext servletContext;

	private final MutableRequest request;

	private final MutableResponse response;

	private final FilterChain chain;
	
    public static final String INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";

	public RequestInfo(ServletContext servletContext, FilterChain chain, MutableRequest request, MutableResponse response) {
		this.servletContext = servletContext;
		this.chain = chain;
		this.request = request;
		this.response = response;
	}

	public FilterChain getChain() {
		return chain;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public MutableRequest getRequest() {
		return request;
	}

	public MutableResponse getResponse() {
		return response;
	}

    public String getRequestedUri() {
        if (request.getAttribute(INCLUDE_REQUEST_URI) != null) {
            return (String) request.getAttribute(INCLUDE_REQUEST_URI);
        }
        String uri = request.getRequestURI().replaceFirst("(?i);jsessionid=.*$", "");
        String contextName = request.getContextPath();
        uri = uri.replaceFirst(contextName, "");
        return uri;
    }

}

/***
 * 
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.PageResult;
import br.com.caelum.vraptor.view.PathResolver;
import br.com.caelum.vraptor.view.ResultException;

/**
 * An implementation of page result which also reads views.properties entries
 * @author guilherme silveira
 *
 */
public class ViewsPropertiesPageResult implements PageResult {

	private final Config config;
	private final HttpServletRequest request;
	private final PathResolver resolver;
	private final ResourceMethod method;
	private final HttpServletResponse response;
	private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

	private static final Logger logger = LoggerFactory.getLogger(ViewsPropertiesPageResult.class);
	private final RequestInfo webRequest;

	public ViewsPropertiesPageResult(Config config, PathResolver resolver, MethodInfo requestInfo,
			RequestInfo webRequest) {
		this.config = config;
		this.webRequest = webRequest;
		this.request = webRequest.getRequest();
		this.resolver = resolver;
		this.method = requestInfo.getResourceMethod();
		this.response = webRequest.getResponse();
	}

	public void forward(String result) {
		try {
			Resource resource = method.getResource();
			if (!Info.isOldComponent(resource)) {
				String forwardPath = resolver.pathFor(method, result);
				request.getRequestDispatcher(forwardPath).forward(request, response);
				return;
			}
			String key = Info.getComponentName(resource.getType()) + "." + Info.getLogicName(method.getMethod()) + "."
					+ result;

			String path = config.getForwardFor(key);

			if (path == null) {
				String forwardPath = resolver.pathFor(method, result);
				request.getRequestDispatcher(forwardPath).forward(request, response);
			} else {
				try {
					result = evaluator.parseExpression(path, webRequest);
				} catch (ExpressionEvaluationException e) {
					throw new ServletException("Unable to redirect while evaluating expression '" + path + "'.", e);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("overriden view found for " + key + " : " + path + " expressed as " + result);
				}
				if (result.startsWith("redirect:")) {
					response.sendRedirect(result.substring(9));
				} else {
					request.getRequestDispatcher(result).forward(request, response);
				}
			}
		} catch (ServletException e) {
			throw new ResultException(e);
		} catch (IOException e) {
			throw new ResultException(e);
		}
	}

	public void include(String result) {
		try {
			request.getRequestDispatcher(resolver.pathFor(method, result)).include(request, response);
		} catch (ServletException e) {
			throw new ResultException(e);
		} catch (IOException e) {
			throw new ResultException(e);
		}
	}

	public void include(String key, Object value) {
		request.setAttribute(key, value);
	}

	public void redirect(String url) {
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			throw new ResultException(e);
		}
	}

}

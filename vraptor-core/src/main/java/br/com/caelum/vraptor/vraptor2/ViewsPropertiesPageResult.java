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

package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.ProxyInvocationException;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.DefaultPageResult;
import br.com.caelum.vraptor.view.PageResult;
import br.com.caelum.vraptor.view.PathResolver;
import br.com.caelum.vraptor.view.ResultException;

/**
 * An implementation of page result which also reads views.properties entries
 * @author guilherme silveira
 *
 */
@Component
public class ViewsPropertiesPageResult implements PageResult {

	private static final Logger logger = LoggerFactory.getLogger(ViewsPropertiesPageResult.class);

	private final Config config;
	private final HttpServletRequest request;
	private final PathResolver resolver;
	private final ResourceMethod method;
	private final HttpServletResponse response;
	private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

	private final RequestInfo webRequest;
	private final MethodInfo info;
	private final Proxifier proxifier;
	private final DefaultPageResult delegate;

	public ViewsPropertiesPageResult(Config config, PathResolver resolver, MethodInfo requestInfo,
			RequestInfo webRequest, MethodInfo info, Proxifier proxifier, DefaultPageResult delegate) {
		this.config = config;
		this.webRequest = webRequest;
		this.info = info;
		this.proxifier = proxifier;
		this.request = webRequest.getRequest();
		this.resolver = resolver;
		this.method = requestInfo.getResourceMethod();
		this.response = webRequest.getResponse();
		this.delegate = delegate;
	}

	public void defaultView() {
		try {
			forwardTo(this.method);
		} catch (ServletException e) {
			throw new ResultException(e);
		} catch (IOException e) {
			throw new ResultException(e);
		}
	}

	private void forwardTo(ResourceMethod method) throws ServletException, IOException {
		if (Info.isOldComponent(method.getResource())) {
			vraptor2ForwardTo(method);
		} else {
			delegate.defaultView();
		}
	}

	private void vraptor2ForwardTo(ResourceMethod method) throws ServletException,
			IOException {
    	logger.debug("Forwading using VRaptor2 URLs");

		String result = info.getResult().toString();
		ResourceClass resource = method.getResource();

		String key = Info.getComponentName(resource.getType()) + "." + Info.getLogicName(method.getMethod()) + "."
				+ result;

		String path = config.getForwardFor(key);

		if (path == null) {
			String forwardPath = resolver.pathFor(method);
        	logger.debug("Forwading to {}", forwardPath);

			request.getRequestDispatcher(forwardPath).forward(request, response);
		} else {
			try {
				result = evaluator.parseExpression(path, webRequest);
			} catch (ExpressionEvaluationException e) {
				throw new ServletException("Unable to redirect while evaluating expression '" + path + "'.", e);
			}
			logger.debug("overriden view found for {} : {} expressed as {}", new Object[] { key, path, result });
			if (result.startsWith("redirect:")) {
				response.sendRedirect(result.substring(9));
			} else {
				request.getRequestDispatcher(result).forward(request, response);
			}
		}
	}

	public <T> T of(final Class<T> controllerType) {
		return proxifier.proxify(controllerType, new MethodInvocation<T>() {
            public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
                try {
                    ResourceMethod resourceMethod = DefaultResourceMethod.instanceFor(controllerType, method);
                    forwardTo(resourceMethod);
                    return null;
                } catch (Exception e) {
                    throw new ProxyInvocationException(e);
				}
            }
        });
	}

	public void include() {
		delegate.include();
	}

	public void redirectTo(String url) {
		delegate.redirectTo(url);
	}

	public void forwardTo(String url) {
        delegate.forwardTo(url);
	}

	public void redirect(String url) {
		this.redirectTo(url);
	}

	public void forward(String url) {
		this.forwardTo(url);
	}

	public void forward() {
		this.defaultView();
	}

}

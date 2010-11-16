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

package br.com.caelum.vraptor.view;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.ProxyInvocationException;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Default page result implementation.
 *
 * @author Guilherme Silveira
 * @author Lucas Cavalcanti
 */
public class DefaultPageResult implements PageResult {

    private final MutableRequest request;
    private final HttpServletResponse response;
    private final ResourceMethod method;
    private final PathResolver resolver;
	private final Proxifier proxifier;

    public DefaultPageResult(MutableRequest req, HttpServletResponse res, MethodInfo requestInfo,
            PathResolver resolver, Proxifier proxifier) {
        this.request = req;
        this.response = res;
		this.proxifier = proxifier;
        this.method = requestInfo.getResourceMethod();
        this.resolver = resolver;
    }

    public void forward() {
        try {
            request.getRequestDispatcher(resolver.pathFor(method)).forward(request, response);
        } catch (ServletException e) {
            throw new ResultException(e);
        } catch (IOException e) {
            throw new ResultException(e);
        }
    }

    public void include() {
        try {
            request.getRequestDispatcher(resolver.pathFor(method)).include(request, response);
        } catch (ServletException e) {
            throw new ResultException(e);
        } catch (IOException e) {
            throw new ResultException(e);
        }
    }

    public void redirect(String url) {
        try {
        	if (url.startsWith("/")) {
				response.sendRedirect(request.getContextPath() + url);
			} else {
				response.sendRedirect(url);
			}
        } catch (IOException e) {
            throw new ResultException(e);
        }
    }

	public void forward(String url) {
        try {
            request.getRequestDispatcher(url).forward(request, response);
        } catch (ServletException e) {
            throw new ResultException(e);
        } catch (IOException e) {
            throw new ResultException(e);
        }
	}

	public <T> T of(final Class<T> controllerType) {
		return proxifier.proxify(controllerType, new MethodInvocation<T>() {
            public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
                try {
                    request.getRequestDispatcher(resolver.pathFor(DefaultResourceMethod.instanceFor(controllerType, method))).forward(request, response);
                    return null;
                } catch (Exception e) {
                    throw new ProxyInvocationException(e);
				}
            }
        });
	}

}

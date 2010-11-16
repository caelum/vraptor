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
import br.com.caelum.vraptor.http.route.MethodNotAllowedException;
import br.com.caelum.vraptor.http.route.ResourceNotFoundException;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.ProxyInvocationException;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Default page result implementation.
 *
 * By default, if you try to redirect or forward to an URI that is matched
 * by a route, an exception will be thrown.
 *
 * If you want to disable this exception, you can override the {@link DefaultPageResult#checkForLogic(String, HttpMethod)}
 * method.
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
	private final Router router;

    public DefaultPageResult(MutableRequest req, HttpServletResponse res, MethodInfo requestInfo,
            PathResolver resolver, Proxifier proxifier, Router router) {
        this.request = req;
        this.response = res;
		this.proxifier = proxifier;
		this.router = router;
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
        	checkForLogic(url, HttpMethod.GET); // redirect only makes sense with GET method
        	if (url.startsWith("/")) {
				response.sendRedirect(request.getContextPath() + url);
			} else {
				response.sendRedirect(url);
			}
        } catch (IOException e) {
            throw new ResultException(e);
        }
    }

	protected void checkForLogic(String url, HttpMethod httpMethod) {
		try {
			ResourceMethod method = router.parse(url, httpMethod, request);
			throw new ResultException("Given uri " + url + " responds to method: " + method.getMethod() + ".\n" +
					"Use result.use(logic()).redirectTo(" + method.getResource().getType().getSimpleName() + ".class)."
					+ method.getMethod().getName() + "() instead.");
		} catch (ResourceNotFoundException e) {
			//Ok
		} catch (MethodNotAllowedException e) {
			//Ok
		}
	}
	public void forward(String url) {
        try {
        	checkForLogic(url, HttpMethod.of(request)); // keep http method
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

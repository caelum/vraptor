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
import java.lang.reflect.Type;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.ProxyInvocationException;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.HttpMethod;

/**
 * The default implementation of LogicResult.<br>
 * Uses cglib to provide proxies for client side redirect (url creation).
 *
 * @author Guilherme Silveira
 */
public class DefaultLogicResult implements LogicResult {

	public static final String FLASH_PARAMETERS = "_vraptor_flash_parameters";

    private final Proxifier proxifier;
    private final Router router;
    private final MutableRequest request;
    private final HttpServletResponse response;

	private final TypeCreator creator;

	private final Container container;

	private final PathResolver resolver;

	private final TypeNameExtractor extractor;

    public DefaultLogicResult(Proxifier proxifier, Router router, MutableRequest request,
            HttpServletResponse response, TypeCreator creator, Container container, PathResolver resolver,
            TypeNameExtractor extractor) {
        this.proxifier = proxifier;
        this.response = response;
        this.request = request;
        this.router = router;
		this.creator = creator;
		this.container = container;
		this.resolver = resolver;
		this.extractor = extractor;
    }

    /**
     * This implementation don't actually use request dispatcher for the forwarding.
     * It runs forwarding logic, and renders its <b>default</b> view.
     */
    public <T> T forwardTo(final Class<T> type) {
        return proxifier.proxify(type, new MethodInvocation<T>() {
            public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
                try {
                	Object result = method.invoke(container.instanceFor(type), args);
                	Type returnType = method.getGenericReturnType();
					if (!(returnType == void.class)) {
                		request.setAttribute(extractor.nameFor(returnType), result);
                	}
                    request.getRequestDispatcher(resolver.pathFor(DefaultResourceMethod.instanceFor(type, method))).forward(request, response);
                    return null;
                } catch (Exception e) {
                    throw new ProxyInvocationException(e);
				}
            }
        });
    }

    private <T> void includeParametersInFlash(final Class<T> type,
    		Method method, Object[] args) {
    	Object params = creator.instanceWithParameters(DefaultResourceMethod.instanceFor(type, method), args);
    	request.getSession().setAttribute(FLASH_PARAMETERS, params);
    }
    public <T> T redirectTo(final Class<T> type) {
        return proxifier.proxify(type, new MethodInvocation<T>() {
            public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
            	if (!acceptsHttpGet(method)) {
            		throw new IllegalArgumentException("Your logic method must accept HTTP GET method if you want to redirect to it");
            	}
                try {
                    String path = request.getContextPath();
                    String url = router.urlFor(type, method, args);
                    includeParametersInFlash(type, method, args);
                    response.sendRedirect(path + url);
                    return null;
                } catch (IOException e) {
                    throw new ProxyInvocationException(e);
                }
            }

        });
    }
    private boolean acceptsHttpGet(Method method) {
    	if (method.isAnnotationPresent(Get.class)) {
			return true;
		}
    	for (HttpMethod httpMethod : HttpMethod.values()) {
			if (method.isAnnotationPresent(httpMethod.getAnnotation())) {
				return false;
			}
		}
    	return true;
    }

}

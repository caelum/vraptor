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
package br.com.caelum.vraptor.view;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.http.route.Router;
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
    private final ServletContext context;
    private final MutableRequest request;
    private final HttpServletResponse response;

	private final TypeCreator creator;

	private final Container container;

	private final PathResolver resolver;

    public DefaultLogicResult(Proxifier proxifier, Router router, ServletContext context, MutableRequest request,
            HttpServletResponse response, TypeCreator creator, Container container, PathResolver resolver) {
        this.proxifier = proxifier;
        this.response = response;
        this.context = context;
        this.request = request;
        this.router = router;
		this.creator = creator;
		this.container = container;
		this.resolver = resolver;
    }
//    private void setHttpMethod(Method method) {
//    	for (HttpMethod httpMethod : HttpMethod.values()) {
//        	if (method.isAnnotationPresent(httpMethod.getAnnotation())) {
//        		request.setParameter("_method", httpMethod.name());
//        		return;
//        	}
//		}
//    }
    public <T> T forwardTo(final Class<T> type) {
        return proxifier.proxify(type, new MethodInvocation<T>() {
            public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
                try {
//                    String url = router.urlFor(type, method, args);
//                    setHttpMethod(method);
//                    includeParametersInFlash(type, method, args);
                    method.invoke(container.instanceFor(type), args);
                    request.getRequestDispatcher(resolver.pathFor(DefaultResourceMethod.instanceFor(type, method))).forward(request, response);
//                    request.getRequestDispatcher(url).forward(request, response);
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
                    String path = context.getContextPath();
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

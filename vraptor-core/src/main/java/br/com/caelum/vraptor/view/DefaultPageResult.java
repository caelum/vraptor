/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
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
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.view;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.MutableRequest;
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
 * @author Guilherme Silveira
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
        	checkForLogic(url);
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new ResultException(e);
        }
    }

	private void checkForLogic(String url) {
		ResourceMethod method = router.parse(url, HttpMethod.GET, request);
		if (method != null) {
			throw new ResultException("Given uri " + url + " responds to method: " + method.getMethod() + ".\n" +
					"Use result.use(logic()).redirectTo(" + method.getResource().getType().getSimpleName() + ".class)."
					 + method.getMethod().getName() + "() instead.");
		}
	}

	public void forward(String url) {
        try {
        	checkForLogic(url);
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

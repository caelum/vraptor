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
package br.com.caelum.vraptor.view.jsp;

import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.LogicResult;
import br.com.caelum.vraptor.view.PathResolver;
import br.com.caelum.vraptor.view.ResultException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A jsp view which can be customized by providing your own PathConstructor.
 *
 * @author Guilherme Silveira
 */
public class DefaultPageResult implements View, PageResult {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ResourceMethod method;
    private final PathResolver resolver;

    public DefaultPageResult(HttpServletRequest req, HttpServletResponse res, RequestInfo requestInfo,
            PathResolver resolver) {
        this.request = req;
        this.response = res;
        this.method = requestInfo.getResourceMethod();
        this.resolver = resolver;
    }

    public static Class<DefaultPageResult> page() {
        return DefaultPageResult.class;
    }

    public static Class<LogicResult> logic() {
        return LogicResult.class;
    }

    public void forward(String result) {
        try {
            request.getRequestDispatcher(resolver.pathFor(method, result)).forward(request, response);
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

}

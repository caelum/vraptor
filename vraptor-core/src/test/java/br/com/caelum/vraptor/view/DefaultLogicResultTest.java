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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.route.Router;

public class DefaultLogicResultTest {

    private Mockery mockery;
    private LogicResult logicResult;
    private Router router;
    private HttpServletResponse response;
    private ServletContext context;
    private HttpServletRequest request;

    public static class MyComponent {
        public void base() {
        }
    }

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.router = mockery.mock(Router.class);
        this.response = mockery.mock(HttpServletResponse.class);
        this.request = mockery.mock(HttpServletRequest.class);
        this.context = mockery.mock(ServletContext.class);
        this.logicResult = new DefaultLogicResult(response, context, request,router);
    }

    @Test
    public void instantiatesUsingTheContainerAndAddsTheExecutionInterceptors() throws NoSuchMethodException, IOException, ServletException {
        final String url = "custom_url";
        mockery.checking(new Expectations() {
            {
                one(router).urlFor(MyComponent.class, MyComponent.class.getDeclaredMethod("base"));
                will(returnValue(url));
                one(request).getRequestDispatcher(url);
                RequestDispatcher dispatcher = mockery.mock(RequestDispatcher.class);
                will(returnValue(dispatcher));
                one(dispatcher).forward(request, response);
            }
        });
        logicResult.redirectServerTo(MyComponent.class).base();
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void clientRedirectingWillRedirectToTranslatedUrl() throws NoSuchMethodException, IOException {
        final String url = "custom_url";
        mockery.checking(new Expectations() {
            {
                one(context).getContextPath(); will(returnValue("/context"));
                one(router).urlFor(MyComponent.class, MyComponent.class.getDeclaredMethod("base"));
                will(returnValue(url));
                one(response).sendRedirect("/context" + url);
            }
        });
        logicResult.redirectClientTo(MyComponent.class).base();
        mockery.assertIsSatisfied();
    }
    
}

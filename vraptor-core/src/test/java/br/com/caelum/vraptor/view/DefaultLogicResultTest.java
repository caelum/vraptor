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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.proxy.DefaultProxifier;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultLogicResultTest {

    private Mockery mockery;
    private LogicResult logicResult;
    private Router router;
    private HttpServletResponse response;
    private ServletContext context;
    private MutableRequest request;
	private TypeCreator creator;

    public static class MyComponent {
        public void base() {
        }

        @Post
        public void annotated() {

        }
        @Get
        public void annotatedWithGet() {

        }
    }

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.router = mockery.mock(Router.class);
        this.response = mockery.mock(HttpServletResponse.class);
        this.request = mockery.mock(MutableRequest.class);
        this.context = mockery.mock(ServletContext.class);
        this.creator = mockery.mock(TypeCreator.class);

        this.logicResult = new DefaultLogicResult(new DefaultProxifier(), router, context, request, response, creator, null, null);
    }

	private void ignoreFlashScope() {
		mockery.checking(new Expectations() {
			{
				HttpSession session = mockery.mock(HttpSession.class);
				ignoring(session);

				allowing(creator).instanceWithParameters(with(any(ResourceMethod.class)), with(any(Object[].class)));
				allowing(request).getSession(); will(returnValue(session));
			}
		});
	}

	@Test
	public void shouldPutParametersOnFlashScopeOnForward() throws Exception {

		shouldPutOnFlashScope();
		logicResult.forwardTo(MyComponent.class).base();
		mockery.assertIsSatisfied();
	}
	@Test
	public void shouldPutParametersOnFlashScopeOnRedirect() throws Exception {

		shouldPutOnFlashScope();
		logicResult.redirectTo(MyComponent.class).base();
		mockery.assertIsSatisfied();
	}

	private void shouldPutOnFlashScope() {
		mockery.checking(new Expectations() {
			{
				HttpSession session = mockery.mock(HttpSession.class);
				one(session).setAttribute(with(equal(DefaultLogicResult.FLASH_PARAMETERS)), with(any(Object.class)));

				one(request).getSession(); will(returnValue(session));

				ignoring(anything());
			}
		});
	}
    @Test
    public void instantiatesUsingTheContainerAndAddsTheExecutionInterceptors() throws NoSuchMethodException, IOException, ServletException {
    	ignoreFlashScope();
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
        logicResult.forwardTo(MyComponent.class).base();
        mockery.assertIsSatisfied();
    }

    @Test
    public void clientRedirectingWillRedirectToTranslatedUrl() throws NoSuchMethodException, IOException {
    	ignoreFlashScope();

        final String url = "custom_url";
        mockery.checking(new Expectations() {
            {
                one(context).getContextPath();
                will(returnValue("/context"));
                one(router).urlFor(MyComponent.class, MyComponent.class.getDeclaredMethod("base"));
                will(returnValue(url));
                one(response).sendRedirect("/context" + url);
            }
        });
        logicResult.redirectTo(MyComponent.class).base();
        mockery.assertIsSatisfied();
    }

    @Test
	public void forwardingToANonGetMethodChangesMethodParameterToTheCorrectHttpMethod() throws Exception {

		mockery.checking(new Expectations() {
			{
				one(request).setParameter("_method", "POST");
				ignoring(anything());
			}
		});

		logicResult.forwardTo(MyComponent.class).annotated();
		mockery.assertIsSatisfied();
	}

    @Test
	public void canRedirectWhenLogicMethodIsNotAnnotatedWithHttpMethods() throws Exception {

		mockery.checking(new Expectations() {
			{
				one(response).sendRedirect(with(any(String.class)));
				ignoring(anything());
			}
		});
    	logicResult.redirectTo(MyComponent.class).base();
	}
    @Test
    public void canRedirectWhenLogicMethodIsAnnotatedWithHttpGetMethod() throws Exception {

    	mockery.checking(new Expectations() {
    		{
    			one(response).sendRedirect(with(any(String.class)));
    			ignoring(anything());
    		}
    	});
    	logicResult.redirectTo(MyComponent.class).annotatedWithGet();
    }
    @Test(expected=IllegalArgumentException.class)
    public void cannotRedirectWhenLogicMethodIsAnnotatedWithAnyHttpMethodButGet() throws Exception {

    	mockery.checking(new Expectations() {
    		{
    			never(response).sendRedirect(with(any(String.class)));
    			ignoring(anything());
    		}
    	});
    	logicResult.redirectTo(MyComponent.class).annotated();
    }
}

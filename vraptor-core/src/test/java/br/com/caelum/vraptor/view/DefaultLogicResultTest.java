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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
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
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.proxy.DefaultProxifier;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultLogicResultTest {

    private Mockery mockery;
    private LogicResult logicResult;
    private Router router;
    private HttpServletResponse response;
    private MutableRequest request;
	private TypeCreator creator;
	private Container container;
	private PathResolver resolver;
	private TypeNameExtractor extractor;

    public static class MyComponent {
    	int calls = 0;
        public void base() {
        	calls++;
        }

        @Post
        public void annotated() {

        }
        @Get
        public void annotatedWithGet() {

        }

        public String returnsValue() {
        	return "A value";
        }
    }

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.router = mockery.mock(Router.class);
        this.response = mockery.mock(HttpServletResponse.class);
        this.request = mockery.mock(MutableRequest.class);
        this.creator = mockery.mock(TypeCreator.class);

        container = mockery.mock(Container.class);
		resolver = mockery.mock(PathResolver.class);
		this.extractor = mockery.mock(TypeNameExtractor.class);

		this.logicResult = new DefaultLogicResult(new DefaultProxifier(), router, request, response,
				creator, container, resolver, extractor);
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
	public void shouldIncludeReturnValueOnForward() throws Exception {
		final MyComponent component = new MyComponent();
		mockery.checking(new Expectations() {
			{
				one(container).instanceFor(MyComponent.class);
				will(returnValue(component));

				one(resolver).pathFor(with(any(ResourceMethod.class)));
				will(returnValue("Abc123"));

				one(request).getRequestDispatcher("Abc123");
				RequestDispatcher dispatcher = mockery.mock(RequestDispatcher.class);
                will(returnValue(dispatcher));
                one(dispatcher).forward(request, response);

                one(extractor).nameFor(String.class); will(returnValue("string"));

                one(request).setAttribute("string", "A value");
			}
		});
		logicResult.forwardTo(MyComponent.class).returnsValue();

		mockery.assertIsSatisfied();
	}
	@Test
	public void shouldExecuteTheLogicAndRedirectToItsViewOnForward() throws Exception {
		final MyComponent component = new MyComponent();
		mockery.checking(new Expectations() {
			{
				one(container).instanceFor(MyComponent.class);
				will(returnValue(component));

				one(resolver).pathFor(with(any(ResourceMethod.class)));
				will(returnValue("Abc123"));

				one(request).getRequestDispatcher("Abc123");
				RequestDispatcher dispatcher = mockery.mock(RequestDispatcher.class);
				will(returnValue(dispatcher));
				one(dispatcher).forward(request, response);
			}
		});
		assertThat(component.calls, is(0));
		logicResult.forwardTo(MyComponent.class).base();
		assertThat(component.calls, is(1));

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
    public void clientRedirectingWillRedirectToTranslatedUrl() throws NoSuchMethodException, IOException {
    	ignoreFlashScope();

        final String url = "custom_url";
        mockery.checking(new Expectations() {
            {
                one(request).getContextPath();
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

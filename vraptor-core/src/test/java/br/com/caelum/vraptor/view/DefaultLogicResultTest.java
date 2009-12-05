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

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.proxy.DefaultProxifier;

public class DefaultLogicResultTest {

    private LogicResult logicResult;

    @Mock private Router router;
    @Mock private HttpServletResponse response;
    @Mock private MutableRequest request;
	@Mock private HttpSession session;
	@Mock private RequestDispatcher dispatcher;

    public static class MyComponent {
    	int calls = 0;
        public void base() {
        	calls++;
        }

        public void withArgs(String arg) {
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
    	MockitoAnnotations.initMocks(this);

    	when(request.getSession()).thenReturn(session);

		this.logicResult = new DefaultLogicResult(new DefaultProxifier(), router, request, response);
    }


	@Test
	public void shouldIncludeArgsOnFlashScopeAndDispatchOnForward() throws Exception {
		givenDispatcherWillBeReturnedWhenRequested();

		logicResult.forwardTo(MyComponent.class).withArgs("Abc");

		verify(session).setAttribute(
				ParametersInstantiatorInterceptor.FLASH_PARAMETERS,
				new Object[] {"Abc"});

		verify(dispatcher).forward(request, response);
	}

	private MyComponent givenDispatcherWillBeReturnedWhenRequested() {
		final MyComponent component = new MyComponent();
		when(router.urlFor(eq(MyComponent.class), any(Method.class), any(Object[].class)))
			.thenReturn("Abc123");
		when(request.getRequestDispatcher("Abc123")).thenReturn(dispatcher);
		return component;
	}

	@Test
	public void shouldPutParametersOnFlashScopeOnRedirect() throws Exception {

		logicResult.redirectTo(MyComponent.class).base();

		verify(session).setAttribute(
				ParametersInstantiatorInterceptor.FLASH_PARAMETERS,
				new Object[0]);
	}

    @Test
    public void clientRedirectingWillRedirectToTranslatedUrl() throws NoSuchMethodException, IOException {

        final String url = "custom_url";
        when(request.getContextPath()).thenReturn("/context");
        when(router.urlFor(MyComponent.class, MyComponent.class.getDeclaredMethod("base"))).thenReturn(url);

        logicResult.redirectTo(MyComponent.class).base();

        verify(response).sendRedirect("/context" + url);
    }

    @Test
	public void canRedirectWhenLogicMethodIsNotAnnotatedWithHttpMethods() throws Exception {

    	logicResult.redirectTo(MyComponent.class).base();

    	verify(response).sendRedirect(any(String.class));
	}
    @Test
    public void canRedirectWhenLogicMethodIsAnnotatedWithHttpGetMethod() throws Exception {
    	logicResult.redirectTo(MyComponent.class).annotatedWithGet();

    	verify(response).sendRedirect(any(String.class));
    }
    @Test
    public void cannotRedirectWhenLogicMethodIsAnnotatedWithAnyHttpMethodButGet() throws Exception {
    	try {
    		logicResult.redirectTo(MyComponent.class).annotated();
    		fail("Expected IllegalArgumentException");
    	} catch (IllegalArgumentException e) {
    		verify(response, never()).sendRedirect(any(String.class));
		}
    }
}

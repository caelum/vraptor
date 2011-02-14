/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
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
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.proxy.CglibProxifier;
import br.com.caelum.vraptor.proxy.ObjenesisInstanceCreator;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;

public class DefaultLogicResultTest {

    private LogicResult logicResult;

    private @Mock Router router;
    private @Mock HttpServletResponse response;
    private @Mock MutableRequest request;
    private @Mock Container container;
    private @Mock PathResolver resolver;
    private @Mock TypeNameExtractor extractor;
    private @Mock RequestDispatcher dispatcher;
    private @Mock FlashScope flash;

    public static class MyComponent {
        int calls = 0;

        public void base() {
            calls++;
        }

        public void withParameter(String a) {

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

        public void throwsValidationException() {
            throw new ValidationException(Collections.<Message>emptyList());
        }
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        this.logicResult = new DefaultLogicResult(new CglibProxifier(new ObjenesisInstanceCreator()), router, request, response, container,
                resolver, extractor, flash);
    }

    @Test
    public void shouldIncludeReturnValueOnForward() throws Exception {
        givenDispatcherWillBeReturnedWhenRequested();
        when(extractor.nameFor(String.class)).thenReturn("string");

        logicResult.forwardTo(MyComponent.class).returnsValue();

        verify(dispatcher).forward(request, response);
        verify(request).setAttribute("string", "A value");
    }

    @Test
    public void shouldExecuteTheLogicAndRedirectToItsViewOnForward() throws Exception {
        final MyComponent component = givenDispatcherWillBeReturnedWhenRequested();

        assertThat(component.calls, is(0));
        logicResult.forwardTo(MyComponent.class).base();
        assertThat(component.calls, is(1));

        verify(dispatcher).forward(request, response);
    }

    private MyComponent givenDispatcherWillBeReturnedWhenRequested() {
        final MyComponent component = new MyComponent();
        when(container.instanceFor(MyComponent.class)).thenReturn(component);
        when(resolver.pathFor(any(ResourceMethod.class))).thenReturn("Abc123");
        when(request.getRequestDispatcher("Abc123")).thenReturn(dispatcher);
        return component;
    }

    @Test
    public void shouldForwardToMethodsDefaultViewWhenResponseIsNotCommited() throws Exception {
        givenDispatcherWillBeReturnedWhenRequested();
        when(response.isCommitted()).thenReturn(false);

        logicResult.forwardTo(MyComponent.class).base();

        verify(dispatcher).forward(request, response);
    }

    @Test
    public void shouldNotForwardToMethodsDefaultViewWhenResponseIsCommited() throws Exception {
        givenDispatcherWillBeReturnedWhenRequested();
        when(response.isCommitted()).thenReturn(true);

        logicResult.forwardTo(MyComponent.class).base();

        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    public void shouldPutParametersOnFlashScopeOnRedirect() throws Exception {

        logicResult.redirectTo(MyComponent.class).withParameter("a");

        verify(flash).includeParameters(any(ResourceMethod.class), eq(new Object[] {"a"}));
    }

    @Test
    public void shouldNotPutParametersOnFlashScopeOnRedirectIfThereAreNoParameters() throws Exception {

        logicResult.redirectTo(MyComponent.class).base();

        verify(flash, never()).includeParameters(any(ResourceMethod.class), any(Object[].class));
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

    @Test(expected=ValidationException.class)
    public void shouldNotWrapValidationExceptionWhenForwarding() throws Exception {
        givenDispatcherWillBeReturnedWhenRequested();

        when(response.isCommitted()).thenReturn(true);

        logicResult.forwardTo(MyComponent.class).throwsValidationException();
    }

}

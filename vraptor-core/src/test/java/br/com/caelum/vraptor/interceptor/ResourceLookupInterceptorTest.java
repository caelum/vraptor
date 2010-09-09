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
package br.com.caelum.vraptor.interceptor;

import java.io.IOException;
import java.util.EnumSet;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.http.route.MethodNotAllowedException;
import br.com.caelum.vraptor.http.route.ResourceNotFoundException;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.MethodNotAllowedHandler;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.ResourceNotFoundHandler;

public class ResourceLookupInterceptorTest {

    private Mockery mockery;
    private UrlToResourceTranslator translator;
    private RequestInfo info;
    private ResourceLookupInterceptor lookup;
    private MutableRequest webRequest;
    private MutableResponse webResponse;
    private MethodInfo methodInfo;
	private ResourceNotFoundHandler notFoundHandler;
	private MethodNotAllowedHandler methodNotAllowedHandler;

    @Before
    public void config() {
        this.mockery = new Mockery();
        this.translator = mockery.mock(UrlToResourceTranslator.class);
        this.webRequest = mockery.mock(MutableRequest.class);
        this.webResponse = mockery.mock(MutableResponse.class);
        this.info = new RequestInfo(null, null, webRequest, webResponse);
        this.methodInfo = mockery.mock(MethodInfo.class);
        this.notFoundHandler = mockery.mock(ResourceNotFoundHandler.class);
        this.methodNotAllowedHandler = mockery.mock(MethodNotAllowedHandler.class);
        this.lookup = new ResourceLookupInterceptor(translator, methodInfo, notFoundHandler, methodNotAllowedHandler, info);
    }

    @Test
    public void shouldHandle404() throws IOException, InterceptionException {
        mockery.checking(new Expectations() {
            {
                one(translator).translate(info);
                will(throwException(new ResourceNotFoundException()));
                one(notFoundHandler).couldntFind(info);
            }
        });
        lookup.intercept(null, null, null);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldHandle405() throws IOException, InterceptionException {
        mockery.checking(new Expectations() {
            {
            	EnumSet<HttpMethod> allowedMethods = EnumSet.of(HttpMethod.GET);

                one(translator).translate(info);
				will(throwException(new MethodNotAllowedException(allowedMethods, HttpMethod.POST.toString())));
                one(methodNotAllowedHandler).deny(info, allowedMethods);
            }
        });
        lookup.intercept(null, null, null);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldUseResourceMethodFoundWithNextInterceptor() throws IOException, InterceptionException {
        final ResourceMethod method = mockery.mock(ResourceMethod.class);
        final InterceptorStack stack = mockery.mock(InterceptorStack.class);
        mockery.checking(new Expectations() {
            {
                one(translator).translate(info);
                will(returnValue(method));
                one(stack).next(method, null);
                one(methodInfo).setResourceMethod(method);
            }
        });
        lookup.intercept(stack, null, null);
        mockery.assertIsSatisfied();
    }

}

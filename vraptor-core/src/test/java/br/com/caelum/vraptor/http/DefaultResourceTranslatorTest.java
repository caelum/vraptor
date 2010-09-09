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

package br.com.caelum.vraptor.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.EnumSet;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.route.MethodNotAllowedException;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultResourceTranslatorTest {

    private Mockery mockery;
    private Router router;
    private DefaultResourceTranslator translator;
    private HttpServletRequest request;
    private VRaptorRequest webRequest;
	private RequestInfo info;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.router = mockery.mock(Router.class);
        this.translator = new DefaultResourceTranslator(router);
        this.request = mockery.mock(HttpServletRequest.class);
        this.webRequest = new VRaptorRequest(request);
        this.info = new RequestInfo(null,null, webRequest,null);
    }

    @Test
    public void handlesInclude() {

        final ResourceMethod expected = mockery.mock(ResourceMethod.class);

        mockery.checking(new Expectations(){{
            exactly(2).of(request).getAttribute(RequestInfo.INCLUDE_REQUEST_URI); will(returnValue("/url"));
            one(request).getMethod(); will(returnValue("POST"));
            one(router).parse("/url", HttpMethod.POST, webRequest); will(returnValue(expected));
            one(request).getParameter("_method"); will(returnValue(null));
        }});

        ResourceMethod resource = translator.translate(info);
        assertThat(resource, is(equalTo(expected)));
        mockery.assertIsSatisfied();

    }

    @Test
    public void canHandleTheCorrectMethod() {

        final ResourceMethod expected = mockery.mock(ResourceMethod.class);

        mockery.checking(new Expectations(){{
            one(request).getAttribute(RequestInfo.INCLUDE_REQUEST_URI); will(returnValue(null));
            one(request).getContextPath();   will(returnValue(""));
            one(request).getRequestURI(); will(returnValue("/url"));
            one(request).getMethod(); will(returnValue("POST"));
            one(router).parse("/url", HttpMethod.POST,webRequest); will(returnValue(expected));
            one(request).getParameter("_method"); will(returnValue(null));
        }});

        ResourceMethod resource = translator.translate(info);
        assertThat(resource, is(equalTo(expected)));
        mockery.assertIsSatisfied();

    }

    @Test
    public void shouldAcceptCaseInsensitiveRequestMethods() {
        final ResourceMethod expected = mockery.mock(ResourceMethod.class);

        mockery.checking(new Expectations(){{
            one(request).getAttribute(RequestInfo.INCLUDE_REQUEST_URI); will(returnValue(null));
            one(request).getContextPath();   will(returnValue(""));
            one(request).getRequestURI(); will(returnValue("/url"));
            one(request).getMethod(); will(returnValue("pOsT"));
            one(router).parse("/url", HttpMethod.POST, webRequest); will(returnValue(expected));
            one(request).getParameter("_method"); will(returnValue(null));
        }});

        ResourceMethod resource = translator.translate(info);
        assertThat(resource, is(equalTo(expected)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldAcceptCaseInsensitiveGetRequestUsingThe_methodParameter() {
        final ResourceMethod expected = mockery.mock(ResourceMethod.class);

        mockery.checking(new Expectations(){{
            one(request).getAttribute(RequestInfo.INCLUDE_REQUEST_URI); will(returnValue(null));
            one(request).getContextPath();   will(returnValue(""));
            one(request).getRequestURI(); will(returnValue("/url"));
            one(request).getParameter("_method"); will(returnValue("gEt"));
            one(request).getMethod(); will(returnValue("POST"));
            one(router).parse("/url", HttpMethod.GET, webRequest); will(returnValue(expected));
        }});

        ResourceMethod resource = translator.translate(info);
        assertThat(resource, is(equalTo(expected)));
        mockery.assertIsSatisfied();
    }


    @Test(expected=MethodNotAllowedException.class)
    public void shouldThrowExceptionWhenRequestANotKnownMethod() {
        final ResourceMethod expected = mockery.mock(ResourceMethod.class);

        mockery.checking(new Expectations(){{
            one(request).getAttribute(RequestInfo.INCLUDE_REQUEST_URI); will(returnValue(null));
            one(request).getContextPath();   will(returnValue(""));
            one(request).getRequestURI(); will(returnValue("/url"));
            one(request).getParameter("_method"); will(returnValue(null));
            one(request).getMethod(); will(returnValue("OPTIONS"));
            one(router).parse("/url", HttpMethod.GET, webRequest); will(returnValue(expected));
            one(router).allowedMethodsFor("/url"); will(returnValue(EnumSet.allOf(HttpMethod.class)));
        }});

        ResourceMethod resource = translator.translate(info);
    }

    @Test
    public void shouldOverrideTheHttpMethodByUsingThe_methodParameter() {

        final ResourceMethod expected = mockery.mock(ResourceMethod.class);

        mockery.checking(new Expectations(){{
            one(request).getAttribute(RequestInfo.INCLUDE_REQUEST_URI); will(returnValue(null));
            one(request).getContextPath();   will(returnValue(""));
            one(request).getRequestURI(); will(returnValue("/url"));
            one(request).getParameter("_method"); will(returnValue("DELETE"));
            one(request).getMethod(); will(returnValue("POST"));
            one(router).parse("/url", HttpMethod.DELETE,webRequest); will(returnValue(expected));
        }});

        ResourceMethod resource = translator.translate(info);
        assertThat(resource, is(equalTo(expected)));
        mockery.assertIsSatisfied();

    }

    @Test
    public void canHandleUrlIfRootContext() {

        final ResourceMethod expected = mockery.mock(ResourceMethod.class);

        mockery.checking(new Expectations(){{
            one(request).getAttribute(RequestInfo.INCLUDE_REQUEST_URI); will(returnValue(null));
            one(request).getContextPath();   will(returnValue(""));
            one(request).getRequestURI(); will(returnValue("/url"));
            one(request).getMethod(); will(returnValue("GET"));
            one(router).parse("/url", HttpMethod.GET,webRequest); will(returnValue(expected));
            one(request).getParameter("_method"); will(returnValue(null));
        }});

        ResourceMethod resource = translator.translate(info);
        assertThat(resource, is(equalTo(expected)));
        mockery.assertIsSatisfied();

    }

    @Test
    public void canHandleUrlIfNonRootContext() {

        final ResourceMethod expected = mockery.mock(ResourceMethod.class);

        mockery.checking(new Expectations(){{
            one(request).getAttribute(RequestInfo.INCLUDE_REQUEST_URI); will(returnValue(null));
            one(request).getContextPath();   will(returnValue("/custom_context"));
            one(request).getRequestURI(); will(returnValue("/custom_context/url"));
            one(request).getMethod(); will(returnValue("GET"));
            one(router).parse("/url", HttpMethod.GET,webRequest); will(returnValue(expected));
            one(request).getParameter("_method"); will(returnValue(null));
        }});

        ResourceMethod resource = translator.translate(info);
        assertThat(resource, is(equalTo(expected)));
        mockery.assertIsSatisfied();

    }

    @Test
    public void canHandleUrlIfPlainRootContext() {

        final ResourceMethod expected = mockery.mock(ResourceMethod.class);

        mockery.checking(new Expectations(){{
            one(request).getAttribute(RequestInfo.INCLUDE_REQUEST_URI); will(returnValue(null));
            one(request).getContextPath();   will(returnValue(""));
            one(request).getRequestURI(); will(returnValue("/"));
            one(request).getMethod(); will(returnValue("GET"));
            one(router).parse("/", HttpMethod.GET,webRequest); will(returnValue(expected));
            one(request).getParameter("_method"); will(returnValue(null));
        }});

        ResourceMethod resource = translator.translate(info);
        assertThat(resource, is(equalTo(expected)));
        mockery.assertIsSatisfied();

    }

    @Test
    public void canHandleComposedUrlIfPlainRootContext() {

        final ResourceMethod expected = mockery.mock(ResourceMethod.class);

        mockery.checking(new Expectations(){{
            one(request).getAttribute(RequestInfo.INCLUDE_REQUEST_URI); will(returnValue(null));
            one(request).getContextPath();   will(returnValue(""));
            one(request).getRequestURI(); will(returnValue("/products/1"));
            one(request).getMethod(); will(returnValue("GET"));
            one(router).parse("/products/1", HttpMethod.GET,webRequest); will(returnValue(expected));
            one(request).getParameter("_method"); will(returnValue(null));
        }});

        ResourceMethod resource = translator.translate(info);
        assertThat(resource, is(equalTo(expected)));
        mockery.assertIsSatisfied();

    }

    @Test
    public void canHandleComposedUrlIfNonRootContext() {

        final ResourceMethod expected = mockery.mock(ResourceMethod.class);

        mockery.checking(new Expectations(){{
            one(request).getAttribute(RequestInfo.INCLUDE_REQUEST_URI); will(returnValue(null));
            one(request).getContextPath();   will(returnValue("/custom_context"));
            one(request).getRequestURI(); will(returnValue("/custom_context/products/1"));
            one(request).getMethod(); will(returnValue("GET"));
            one(router).parse("/products/1", HttpMethod.GET,webRequest); will(returnValue(expected));
            one(request).getParameter("_method"); will(returnValue(null));
        }});

        ResourceMethod resource = translator.translate(info);
        assertThat(resource, is(equalTo(expected)));
        mockery.assertIsSatisfied();

    }
    @Test
    public void canHandleUrlWithAppendedJSessionID() {

    	final ResourceMethod expected = mockery.mock(ResourceMethod.class);

    	mockery.checking(new Expectations(){{
    		allowing(request).getAttribute(RequestInfo.INCLUDE_REQUEST_URI); will(returnValue(null));
    		allowing(request).getContextPath();   will(returnValue("/custom_context"));
    		one(request).getRequestURI(); will(returnValue("/custom_context/products/1;jsessionid=aslfasfaslkj22234lkjsdfaklsf"));
    		one(request).getRequestURI(); will(returnValue("/custom_context/products/1;JSESSIONID=aslfasfaslkj22234lkjsdfaklsf"));
    		one(request).getRequestURI(); will(returnValue("/custom_context/products/1;jsessionID=aslfasfaslkj22234lkjsdfaklsf"));
    		allowing(request).getMethod(); will(returnValue("GET"));
    		allowing(router).parse("/products/1", HttpMethod.GET,webRequest); will(returnValue(expected));
    		allowing(request).getParameter("_method"); will(returnValue(null));
    	}});

    	assertThat(translator.translate(info), is(equalTo(expected)));
    	assertThat(translator.translate(info), is(equalTo(expected)));
    	assertThat(translator.translate(info), is(equalTo(expected)));

    	mockery.assertIsSatisfied();

    }

    @Test
    public void canHandleUrlIfNonRootContextButPlainRequest() {

        final ResourceMethod expected = mockery.mock(ResourceMethod.class);

        mockery.checking(new Expectations(){{
            one(request).getAttribute(RequestInfo.INCLUDE_REQUEST_URI); will(returnValue(null));
            one(request).getContextPath();   will(returnValue("/custom_context"));
            one(request).getRequestURI(); will(returnValue("/custom_context/"));
            one(request).getMethod(); will(returnValue("GET"));
            one(router).parse("/", HttpMethod.GET,webRequest); will(returnValue(expected));
            one(request).getParameter("_method"); will(returnValue(null));
        }});

        ResourceMethod resource = translator.translate(info);
        assertThat(resource, is(equalTo(expected)));
        mockery.assertIsSatisfied();

    }

}

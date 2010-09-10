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
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.route.MethodNotAllowedException;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultResourceTranslatorTest {

    private @Mock Router router;
    private @Mock HttpServletRequest request;

    private @Mock ResourceMethod method;

    private VRaptorRequest webRequest;
	private RequestInfo info;


	private DefaultResourceTranslator translator;

    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);

        this.webRequest = new VRaptorRequest(request);
        this.translator = new DefaultResourceTranslator(router);
        this.info = new RequestInfo(null,null, webRequest,null);
        when(request.getContextPath()).thenReturn("");
    }

    @Test
    public void handlesInclude() {

    	when(request.getAttribute(RequestInfo.INCLUDE_REQUEST_URI)).thenReturn("/url");
    	when(request.getMethod()).thenReturn("POST");
    	when(router.parse("/url", HttpMethod.POST, webRequest)).thenReturn(method);

        ResourceMethod resource = translator.translate(info);
        assertThat(resource, is(sameInstance(method)));
    }

    @Test
    public void canHandleTheCorrectMethod() {

    	when(request.getRequestURI()).thenReturn("/url");
    	when(request.getMethod()).thenReturn("POST");
    	when(router.parse("/url", HttpMethod.POST,webRequest)).thenReturn(method);

        ResourceMethod resource = translator.translate(info);
        assertThat(resource, is(equalTo(method)));
    }

    @Test
    public void shouldAcceptCaseInsensitiveRequestMethods() {
    	when(request.getRequestURI()).thenReturn("/url");
    	when(request.getMethod()).thenReturn("pOsT");
    	when(router.parse("/url", HttpMethod.POST,webRequest)).thenReturn(method);

        ResourceMethod resource = translator.translate(info);

        assertThat(resource, is(equalTo(method)));
    }

    @Test
    public void shouldAcceptCaseInsensitiveGetRequestUsingThe_methodParameter() {
    	when(request.getRequestURI()).thenReturn("/url");
    	when(request.getParameter("_method")).thenReturn("gEt");
    	when(request.getMethod()).thenReturn("POST");
    	when(router.parse("/url", HttpMethod.GET, webRequest)).thenReturn(method);

        ResourceMethod resource = translator.translate(info);
        assertThat(resource, is(equalTo(method)));
    }


    @Test(expected=MethodNotAllowedException.class)
    public void shouldThrowExceptionWhenRequestANotKnownMethod() {
    	when(request.getRequestURI()).thenReturn("/url");
    	when(request.getMethod()).thenReturn("COOK");
    	when(router.parse(anyString(), any(HttpMethod.class), any(MutableRequest.class))).thenReturn(method);

        translator.translate(info);
    }

    @Test
    public void shouldOverrideTheHttpMethodByUsingThe_methodParameter() {
    	when(request.getRequestURI()).thenReturn("/url");
    	when(request.getParameter("_method")).thenReturn("DELETE");
    	when(request.getMethod()).thenReturn("POST");
    	when(router.parse("/url", HttpMethod.DELETE, webRequest)).thenReturn(method);

        ResourceMethod resource = translator.translate(info);
        assertThat(resource, is(equalTo(method)));
    }

    @Test
    public void canHandleUrlIfRootContext() {
    	when(request.getRequestURI()).thenReturn("/url");
    	when(request.getContextPath()).thenReturn("");
    	when(request.getMethod()).thenReturn("GET");
    	when(router.parse("/url", HttpMethod.GET, webRequest)).thenReturn(method);

    	ResourceMethod resource = translator.translate(info);
    	assertThat(resource, is(equalTo(method)));

    }

    @Test
    public void canHandleUrlIfNonRootContext() {
    	when(request.getRequestURI()).thenReturn("/custom_context/url");
    	when(request.getContextPath()).thenReturn("/custom_context");
    	when(request.getMethod()).thenReturn("GET");
    	when(router.parse("/url", HttpMethod.GET, webRequest)).thenReturn(method);

    	ResourceMethod resource = translator.translate(info);
    	assertThat(resource, is(equalTo(method)));
    }

    @Test
    public void canHandleUrlIfPlainRootContext() {
    	when(request.getRequestURI()).thenReturn("/");
    	when(request.getMethod()).thenReturn("GET");
    	when(router.parse("/", HttpMethod.GET, webRequest)).thenReturn(method);

    	ResourceMethod resource = translator.translate(info);
    	assertThat(resource, is(equalTo(method)));
   	}

    @Test
    public void canHandleComposedUrlIfPlainRootContext() {
    	when(request.getRequestURI()).thenReturn("/products/1");
    	when(request.getMethod()).thenReturn("GET");
    	when(router.parse("/products/1", HttpMethod.GET, webRequest)).thenReturn(method);

    	ResourceMethod resource = translator.translate(info);
    	assertThat(resource, is(equalTo(method)));
    }

    @Test
    public void canHandleComposedUrlIfNonRootContext() {
    	when(request.getRequestURI()).thenReturn("/custom_context/products/1");
    	when(request.getContextPath()).thenReturn("/custom_context");
    	when(request.getMethod()).thenReturn("GET");
    	when(router.parse("/products/1", HttpMethod.GET, webRequest)).thenReturn(method);

    	ResourceMethod resource = translator.translate(info);
    	assertThat(resource, is(equalTo(method)));
    }
    @Test
    public void canHandleUrlWithAppendedJSessionID() {
    	when(request.getRequestURI()).thenReturn(
    			"/custom_context/products/1;jsessionid=aslfasfaslkj22234lkjsdfaklsf",
    			"/custom_context/products/1;JSESSIONID=aslfasfaslkj22234lkjsdfaklsf",
    			"/custom_context/products/1;jsessionID=aslfasfaslkj22234lkjsdfaklsf");
    	when(request.getContextPath()).thenReturn("/custom_context");
    	when(request.getMethod()).thenReturn("GET");
    	when(router.parse("/products/1", HttpMethod.GET, webRequest)).thenReturn(method);

    	assertThat(translator.translate(info), is(equalTo(method)));
    	assertThat(translator.translate(info), is(equalTo(method)));
    	assertThat(translator.translate(info), is(equalTo(method)));

    }

    @Test
    public void canHandleUrlIfNonRootContextButPlainRequest() {
    	when(request.getRequestURI()).thenReturn("/custom_context/");
    	when(request.getContextPath()).thenReturn("/custom_context");
    	when(request.getMethod()).thenReturn("GET");
    	when(router.parse("/", HttpMethod.GET, webRequest)).thenReturn(method);

    	ResourceMethod resource = translator.translate(info);
    	assertThat(resource, is(equalTo(method)));
    }

}

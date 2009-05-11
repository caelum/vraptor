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
package br.com.caelum.vraptor.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultResourceTranslatorTest {

	private Mockery mockery;
	private Router registry;
	private DefaultResourceTranslator translator;
	private HttpServletRequest request;
	private VRaptorRequest webRequest;

	@Before
	public void setup() {
		this.mockery = new Mockery();
		this.registry = mockery.mock(Router.class);
		this.translator = new DefaultResourceTranslator(registry);
		this.request = mockery.mock(HttpServletRequest.class);
        this.webRequest = new VRaptorRequest(request);
	}

    @Test
    public void handlesInclude() {

        final ResourceMethod expected = mockery.mock(ResourceMethod.class);

        mockery.checking(new Expectations(){{
            exactly(2).of(request).getAttribute(DefaultResourceTranslator.INCLUDE_REQUEST_URI); will(returnValue("/url"));
            one(request).getMethod(); will(returnValue("POST"));
            one(registry).parse("/url", HttpMethod.POST, webRequest); will(returnValue(expected));
			one(request).getParameter("_method"); will(returnValue(null));
        }});

        ResourceMethod resource = translator.translate(webRequest);
        assertThat(resource, is(equalTo(expected)));
        mockery.assertIsSatisfied();

    }

	@Test
	public void canHandleTheCorrectMethod() {

		final ResourceMethod expected = mockery.mock(ResourceMethod.class);

		mockery.checking(new Expectations(){{
		    one(request).getAttribute(DefaultResourceTranslator.INCLUDE_REQUEST_URI); will(returnValue(null));
			one(request).getRequestURI(); will(returnValue("/url"));
			one(request).getMethod(); will(returnValue("POST"));
			one(registry).parse("/url", HttpMethod.POST,webRequest); will(returnValue(expected));
			one(request).getParameter("_method"); will(returnValue(null));
		}});

		ResourceMethod resource = translator.translate(webRequest);
		assertThat(resource, is(equalTo(expected)));
		mockery.assertIsSatisfied();

	}

    @Test
    public void shouldAcceptCaseInsensitiveRequestMethods() {
        final ResourceMethod expected = mockery.mock(ResourceMethod.class);

        mockery.checking(new Expectations(){{
            one(request).getAttribute(DefaultResourceTranslator.INCLUDE_REQUEST_URI); will(returnValue(null));
            one(request).getRequestURI(); will(returnValue("/url"));
            one(request).getMethod(); will(returnValue("pOsT"));
            one(registry).parse("/url", HttpMethod.POST, webRequest); will(returnValue(expected));
            one(request).getParameter("_method"); will(returnValue(null));
        }});

        ResourceMethod resource = translator.translate(webRequest);
        assertThat(resource, is(equalTo(expected)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldAcceptCaseInsensitiveGetRequestUsingThe_methodParameter() {
        final ResourceMethod expected = mockery.mock(ResourceMethod.class);

        mockery.checking(new Expectations(){{
            one(request).getAttribute(DefaultResourceTranslator.INCLUDE_REQUEST_URI); will(returnValue(null));
            one(request).getRequestURI(); will(returnValue("/url"));
            one(request).getParameter("_method"); will(returnValue("gEt"));
            one(registry).parse("/url", HttpMethod.GET, webRequest); will(returnValue(expected));
        }});

        ResourceMethod resource = translator.translate(webRequest);
        assertThat(resource, is(equalTo(expected)));
        mockery.assertIsSatisfied();
    }

	@Test
	public void shouldOverrideTheHttpMethodByUsingThe_methodParameter() {

		final ResourceMethod expected = mockery.mock(ResourceMethod.class);

		mockery.checking(new Expectations(){{
		    one(request).getAttribute(DefaultResourceTranslator.INCLUDE_REQUEST_URI); will(returnValue(null));
			one(request).getRequestURI(); will(returnValue("/url"));
			one(request).getParameter("_method"); will(returnValue("DELETE"));
			one(registry).parse("/url", HttpMethod.DELETE,webRequest); will(returnValue(expected));
		}});

		ResourceMethod resource = translator.translate(webRequest);
		assertThat(resource, is(equalTo(expected)));
		mockery.assertIsSatisfied();

	}

	@Test
	public void canHandleUrlIfRootContext() {

		final ResourceMethod expected = mockery.mock(ResourceMethod.class);

		mockery.checking(new Expectations(){{
            one(request).getAttribute(DefaultResourceTranslator.INCLUDE_REQUEST_URI); will(returnValue(null));
			one(request).getRequestURI(); will(returnValue("/url"));
			one(request).getMethod(); will(returnValue("GET"));
			one(registry).parse("/url", HttpMethod.GET,webRequest); will(returnValue(expected));
			one(request).getParameter("_method"); will(returnValue(null));
		}});

		ResourceMethod resource = translator.translate(webRequest);
		assertThat(resource, is(equalTo(expected)));
		mockery.assertIsSatisfied();

	}

	@Test
	public void canHandleUrlIfNonRootContext() {

		final ResourceMethod expected = mockery.mock(ResourceMethod.class);

		mockery.checking(new Expectations(){{
            one(request).getAttribute(DefaultResourceTranslator.INCLUDE_REQUEST_URI); will(returnValue(null));
			one(request).getRequestURI(); will(returnValue("/custom_context/url"));
			one(request).getMethod(); will(returnValue("GET"));
			one(registry).parse("/url", HttpMethod.GET,webRequest); will(returnValue(expected));
			one(request).getParameter("_method"); will(returnValue(null));
		}});

		ResourceMethod resource = translator.translate(webRequest);
		assertThat(resource, is(equalTo(expected)));
		mockery.assertIsSatisfied();

	}

	@Test
	public void canHandleUrlIfPlainRootContext() {

		final ResourceMethod expected = mockery.mock(ResourceMethod.class);

		mockery.checking(new Expectations(){{
            one(request).getAttribute(DefaultResourceTranslator.INCLUDE_REQUEST_URI); will(returnValue(null));
			one(request).getRequestURI(); will(returnValue("/"));
			one(request).getMethod(); will(returnValue("GET"));
			one(registry).parse("/", HttpMethod.GET,webRequest); will(returnValue(expected));
			one(request).getParameter("_method"); will(returnValue(null));
		}});

		ResourceMethod resource = translator.translate(webRequest);
		assertThat(resource, is(equalTo(expected)));
		mockery.assertIsSatisfied();

	}

	@Test
	public void canHandleUrlIfNonRootContextButPlainRequest() {

		final ResourceMethod expected = mockery.mock(ResourceMethod.class);

		mockery.checking(new Expectations(){{
            one(request).getAttribute(DefaultResourceTranslator.INCLUDE_REQUEST_URI); will(returnValue(null));
			one(request).getRequestURI(); will(returnValue("/custom_context/"));
			one(request).getMethod(); will(returnValue("GET"));
			one(registry).parse("/", HttpMethod.GET,webRequest); will(returnValue(expected));
			one(request).getParameter("_method"); will(returnValue(null));
		}});

		ResourceMethod resource = translator.translate(webRequest);
		assertThat(resource, is(equalTo(expected)));
		mockery.assertIsSatisfied();

	}

}

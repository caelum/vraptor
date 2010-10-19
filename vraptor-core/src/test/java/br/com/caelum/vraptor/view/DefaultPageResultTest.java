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

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.DefaultMethodInfo;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultPageResultTest {

    private Mockery mockery;
    private MutableRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;
    private ResourceMethod method;
    private PathResolver fixedResolver;
    private MethodInfo requestInfo;
	private DefaultPageResult view;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        request = mockery.mock(MutableRequest.class);
        response = mockery.mock(HttpServletResponse.class);
        dispatcher = mockery.mock(RequestDispatcher.class);
        method = DefaultResourceMethod.instanceFor(AnyResource.class, AnyResource.class.getDeclaredMethods()[0]);
        requestInfo = new DefaultMethodInfo();
        requestInfo.setResourceMethod(method);
        fixedResolver = new PathResolver() {
            public String pathFor(ResourceMethod method) {
                return "fixed";
            }
        };
		view = new DefaultPageResult(request, response, requestInfo, fixedResolver, mockery.mock(Proxifier.class));
    }

    public static class AnyResource {
    	public void method() {
    	}
    }
    @Test
	public void shouldRedirectIncludingContext() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(request).getContextPath(); will(returnValue("/context"));

				one(response).sendRedirect("/context/any/url");
			}
		});

		view.redirectTo("/any/url");
		mockery.assertIsSatisfied();
	}
    @Test
    public void shouldNotIncludeContextPathIfURIIsAbsolute() throws Exception {
    	mockery.checking(new Expectations() {
    		{
    			never(request).getContextPath();

    			one(response).sendRedirect("http://vraptor.caelum.com.br");
    		}
    	});

    	view.redirectTo("http://vraptor.caelum.com.br");
    	mockery.assertIsSatisfied();
    }
    @Test
    public void shouldForwardToGivenURI() throws Exception {
    	mockery.checking(new Expectations() {
    		{
    			one(request).getRequestDispatcher("/any/url");
                will(returnValue(dispatcher));
                one(dispatcher).forward(request, response);

    		}
    	});

    	view.forwardTo("/any/url");
    	mockery.assertIsSatisfied();
    }

    @Test
    public void shouldAllowCustomPathResolverWhileForwarding() throws ServletException, IOException {
        mockery.checking(new Expectations() {
            {
                one(request).getRequestDispatcher("fixed");
                will(returnValue(dispatcher));
                one(dispatcher).forward(request, response);
            }
        });
        view.defaultView();
        mockery.assertIsSatisfied();
    }


    @Test
    public void shouldAllowCustomPathResolverWhileIncluding() throws ServletException, IOException {
        mockery.checking(new Expectations() {
            {
                one(request).getRequestDispatcher("fixed");
                will(returnValue(dispatcher));
                one(dispatcher).include(request, response);
            }
        });
        view.include();
        mockery.assertIsSatisfied();
    }

}

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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.core.DefaultMethodInfo;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.proxy.ObjenesisInstanceCreator;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.ProxyInvocationException;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultPageResultTest {

    private @Mock MutableRequest request;
    private @Mock MutableResponse response;
    private @Mock RequestDispatcher dispatcher;
    private Proxifier proxifier;
    private ResourceMethod method;
    private PathResolver fixedResolver;
    private MethodInfo requestInfo;
	private DefaultPageResult view;
	
    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
        method = DefaultResourceMethod.instanceFor(AnyResource.class, AnyResource.class.getDeclaredMethods()[0]);
        proxifier = new JavassistProxifier(new ObjenesisInstanceCreator());
        requestInfo = new DefaultMethodInfo();
        requestInfo.setResourceMethod(method);
        fixedResolver = new PathResolver() {
            public String pathFor(ResourceMethod method) {
                return "fixed";
            }
        };
		view = new DefaultPageResult(request, response, requestInfo, fixedResolver, proxifier);
    }

    public static class AnyResource {
    	public void method() {
    	}
    }
    
    @Test
	public void shouldRedirectIncludingContext() throws Exception {
    	when(request.getContextPath()).thenReturn("/context");
    	
		view.redirectTo("/any/url");
		
    	verify(response).sendRedirect("/context/any/url");
	}
    
    @Test
    public void shouldNotIncludeContextPathIfURIIsAbsolute() throws Exception {
    	view.redirectTo("http://vraptor.caelum.com.br");
    	
    	verify(request, never()).getContextPath();
    	verify(response, only()).sendRedirect("http://vraptor.caelum.com.br");
    }
    
	@Test(expected=ResultException.class)
    public void shouldThrowResultExceptionIfIOExceptionOccursWhileRedirect() throws Exception {
    	doThrow(new IOException()).when(response).sendRedirect(anyString());
    	
		view.redirectTo("/any/url");
    }
    
    @Test
    public void shouldForwardToGivenURI() throws Exception {
    	when(request.getRequestDispatcher("/any/url")).thenReturn(dispatcher);
    	
    	view.forwardTo("/any/url");
    	verify(dispatcher, only()).forward(request, response);
    }
    
    
	@Test(expected=ResultException.class)
    public void shouldThrowResultExceptionIfServletExceptionOccursWhileForwarding() throws Exception {
    	when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    	doThrow(new ServletException()).when(dispatcher).forward(request, response);
    	
    	view.forwardTo("/any/url");
    }

	@Test(expected=ResultException.class)
    public void shouldThrowResultExceptionIfIOExceptionOccursWhileForwarding() throws Exception {
    	when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    	doThrow(new IOException()).when(dispatcher).forward(request, response);
    	
    	view.forwardTo("/any/url");
    }
    
    @Test
    public void shouldAllowCustomPathResolverWhileForwardingView() throws ServletException, IOException {
    	when(request.getRequestDispatcher("fixed")).thenReturn(dispatcher);
    	
        view.defaultView();
        
    	verify(dispatcher, only()).forward(request, response);
    }
    
	@Test(expected=ResultException.class)
    public void shouldThrowResultExceptionIfServletExceptionOccursWhileForwardingView() throws Exception {
    	when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    	doThrow(new ServletException()).when(dispatcher).forward(request, response);
    	
        view.defaultView();
    }

	@Test(expected=ResultException.class)
    public void shouldThrowResultExceptionIfIOExceptionOccursWhileForwardingView() throws Exception {
    	when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    	doThrow(new IOException()).when(dispatcher).forward(request, response);
    	
        view.defaultView();
    }

    @Test
    public void shouldAllowCustomPathResolverWhileIncluding() throws ServletException, IOException {
    	when(request.getRequestDispatcher("fixed")).thenReturn(dispatcher);
    	
        view.include();
        
    	verify(dispatcher, only()).include(request, response);
    }

	@Test(expected=ResultException.class)
    public void shouldThrowResultExceptionIfServletExceptionOccursWhileIncluding() throws Exception {
    	when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    	doThrow(new ServletException()).when(dispatcher).include(request, response);
    	
        view.include();
    }

	@Test(expected=ResultException.class)
    public void shouldThrowResultExceptionIfIOExceptionOccursWhileIncluding() throws Exception {
    	when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    	doThrow(new IOException()).when(dispatcher).include(request, response);
    	
        view.include();
    }
	
	@Test
	public void shoudNotExecuteLogicWhenUsingResultOf() {
		when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
		try {
			view.of(SimpleController.class).notAllowedMethod();
		} catch (UnsupportedOperationException e) {
			fail("Method should not be executed");
		}
	}

	@Test(expected = ProxyInvocationException.class)
	public void shoudThrowProxyInvocationExceptionIfAndExceptionOccursWhenUsingResultOf() {
		doThrow(new NullPointerException()).when(request).getRequestDispatcher(anyString());
		view.of(SimpleController.class).notAllowedMethod();
	}

	public static class SimpleController {
		public void notAllowedMethod() {
			throw new UnsupportedOperationException();
		}
	}
}

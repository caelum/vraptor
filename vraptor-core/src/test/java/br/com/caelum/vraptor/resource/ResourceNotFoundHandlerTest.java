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
package br.com.caelum.vraptor.resource;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;

public class ResourceNotFoundHandlerTest {

	private ResourceNotFoundHandler notFoundHandler;
	private @Mock MutableRequest webRequest;
	private @Mock MutableResponse webResponse;
	private @Mock FilterChain chain;
	private RequestInfo request;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	request = new RequestInfo(null, chain, webRequest, webResponse);
		notFoundHandler = new DefaultResourceNotFoundHandler();
	}

	@Test
	public void couldntFindDefersRequestToContainer() throws Exception {
		notFoundHandler.couldntFind(request);
		verify(chain, only()).doFilter(webRequest, webResponse);
	}
	
	@Test(expected=InterceptionException.class)
	public void shouldThrowInterceptionExceptionIfIOExceptionOccurs() throws Exception {
		doThrow(new IOException()).when(chain).doFilter(webRequest, webResponse);
		notFoundHandler.couldntFind(request);
	}
	
	@Test(expected=InterceptionException.class)
	public void shouldThrowInterceptionExceptionIfServletExceptionOccurs() throws Exception {
		doThrow(new ServletException()).when(chain).doFilter(webRequest, webResponse);
		notFoundHandler.couldntFind(request);
	}
	
}

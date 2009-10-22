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

import javax.servlet.FilterChain;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;

public class ResourceNotFoundHandlerTest {

	private ResourceNotFoundHandler notFoundHandler;
	private Mockery mockery;
    private MutableRequest webRequest;
    private MutableResponse webResponse;
    private RequestInfo request;
	private FilterChain chain;

    @Before
	public void setUp() {
		this.mockery = new Mockery();
        this.webRequest = mockery.mock(MutableRequest.class);
        this.webResponse = mockery.mock(MutableResponse.class);
        this.chain = mockery.mock(FilterChain.class);
        this.request = new RequestInfo(null, chain, webRequest, webResponse);
		this.notFoundHandler = new DefaultResourceNotFoundHandler();
	}

	@Test
	public void couldntFindDefersRequestToContainer() throws Exception {
        mockery.checking(new Expectations() {
            {
                one(chain).doFilter(webRequest, webResponse);
            }
        });
		notFoundHandler.couldntFind(request);
        mockery.assertIsSatisfied();
	}
}

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

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.VRaptorRequest;
import br.com.caelum.vraptor.http.route.Router;

public class CacheBasedRouterTest {

    private Mockery mockery;
    private CacheBasedRouter registry;
    private Router delegate;
    private ResourceMethod resource;
	private VRaptorRequest webRequest;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.delegate = mockery.mock(Router.class);
        this.resource = mockery.mock(ResourceMethod.class);
        this.registry = new CacheBasedRouter(delegate);
        this.webRequest = new VRaptorRequest(mockery.mock(HttpServletRequest.class));
        mockery.checking(new Expectations() {
            {
                one(delegate).parse("dog", HttpMethod.POST, webRequest); will(returnValue(resource));
            }
        });
    }

    @Test
    public void shouldUseTheProvidedResourceDuringFirstRequest() {
        ResourceMethod found = registry.parse("dog", HttpMethod.POST, webRequest);
        MatcherAssert.assertThat(found, Matchers.is(Matchers.equalTo(this.resource)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldUseTheSameResourceOnFurtherRequests() {
        ResourceMethod found = registry.parse("dog", HttpMethod.POST, webRequest);
        MatcherAssert.assertThat(registry.parse("dog", HttpMethod.POST, webRequest), Matchers.is(Matchers.equalTo(found)));
        mockery.assertIsSatisfied();
    }

}

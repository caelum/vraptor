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

package br.com.caelum.vraptor.vraptor2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;
import org.vraptor.annotations.Component;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.route.DefaultRouter;
import br.com.caelum.vraptor.http.route.NoRoutesConfiguration;
import br.com.caelum.vraptor.http.route.NoTypeFinder;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.proxy.DefaultProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class ComponentRoutesCreatorTest {

    private VRaptorMockery mockery;
	private DefaultRouter router;
	private MutableRequest request;
    private Proxifier proxifier;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.request = mockery.mock(MutableRequest.class);
        this.proxifier = new DefaultProxifier();
        this.router = new DefaultRouter(new NoRoutesConfiguration(), new ComponentRoutesParser(proxifier, new NoTypeFinder()), proxifier, null, new NoTypeFinder());
    }

    class NonVRaptorComponent {
        public void name() {
        }
    }

    @br.com.caelum.vraptor.Resource
    class VRaptor3Component {
        public void name() {
        }
    }

    @Test
    public void shouldUseVRaptor3AlgorithmIfNotAVRaptor2Component() throws SecurityException, NoSuchMethodException {
        final ResourceClass resource = mockery.resource(VRaptor3Component.class);
        this.router.register(resource);
        assertThat(router.parse("/vRaptor3Component/name", HttpMethod.POST, request), is(VRaptorMatchers.resourceMethod(VRaptor3Component.class.getMethod("name"))));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldReturnNullIfNotFound() throws SecurityException, NoSuchMethodException {
        final ResourceClass resource = mockery.resource(NonVRaptorComponent.class);
        this.router.register(resource);
        assertThat(router.parse("/NonVRaptorComponent/name", HttpMethod.POST, request), is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Component
    static class MyResource {
        public static void ignorableStatic() {
        }

        protected void ignorableProtected() {
        }

        @Path("/findable")
        public void findable() {
        }
        public String getValue() {
        	return "";
        }
    }

    @Test
    public void ignoresNonPublicMethod() {
        final ResourceClass resource = mockery.resource(MyResource.class);
        this.router.register(resource);
        assertThat(router.parse("/MyResource.ignorableStatic.logic", HttpMethod.POST, request), is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void ignoresGetters() {
        final ResourceClass resource = mockery.resource(MyResource.class);
        this.router.register(resource);
        assertThat(router.parse("/MyResource.getValue.logic", HttpMethod.POST, request), is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void ignoresStaticMethod() {
        final ResourceClass resource = mockery.resource(MyResource.class);
        this.router.register(resource);
        assertThat(router.parse("/MyResource.ignorableProtected.logic", HttpMethod.POST, request), is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void returnsNullIfNothingFound() {
        final ResourceClass resource = mockery.resource(MyResource.class);
        this.router.register(resource);
        assertThat(router.parse("/MyResource.unfindable.logic", HttpMethod.POST, request), is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void returnsTheCorrectDefaultResourceMethodIfFound() throws SecurityException, NoSuchMethodException {
        final ResourceClass resource = mockery.resource(MyResource.class);
        this.router.register(resource);
        assertThat(router.parse("/MyResource.findable.logic", HttpMethod.POST, request), is(VRaptorMatchers.resourceMethod(MyResource.class.getMethod("findable"))));
        mockery.assertIsSatisfied();
    }

}

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
        this.router = new DefaultRouter(new NoRoutesConfiguration(), new ComponentRoutesParser(proxifier), null, proxifier, null);
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

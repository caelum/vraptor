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
package br.com.caelum.vraptor.http.route;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Head;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.proxy.DefaultProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class PathAnnotationRoutesParserTest {

    private VRaptorMockery mockery;
    private ResourceClass resource;
    private DefaultRouter router;
    private MutableRequest request;
    private Proxifier proxifier;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.resource = mockery.resource(Clients.class);
        this.request = mockery.mock(MutableRequest.class);
        this.proxifier = new DefaultProxifier();
        this.router = new DefaultRouter(new NoRoutesConfiguration(), new PathAnnotationRoutesParser(proxifier, new NoTypeFinder()),
        		proxifier, null, new NoTypeFinder());
        router.register(resource);
    }

    @Test
    public void findsTheCorrectAnnotatedMethodIfThereIsNoWebMethodAnnotationPresent() throws SecurityException,
            NoSuchMethodException {
        ResourceMethod method = router.parse("/clients", HttpMethod.POST, request);
        assertThat(method.getMethod(), is(equalTo(Clients.class.getMethod("list"))));
        mockery.assertIsSatisfied();
    }


    @Test
    public void suportsTheDefaultNameForANonAnnotatedMethod() throws SecurityException,
            NoSuchMethodException {
        ResourceMethod method = router.parse("/clients", HttpMethod.POST, request);
        assertThat(method.getMethod(), is(equalTo(Clients.class.getMethod("list"))));
        mockery.assertIsSatisfied();
    }

    @Test
    public void ignoresTheControllerSuffixForANonAnnotatedMethod() throws SecurityException,
            NoSuchMethodException {
        ResourceMethod method = router.parse("/clients", HttpMethod.POST, request);
        assertThat(method.getMethod(), is(equalTo(Clients.class.getMethod("list"))));
        mockery.assertIsSatisfied();
    }

    @Test
    public void matchesWhenUsingAWildcard() throws SecurityException, NoSuchMethodException {
        ResourceMethod method = router.parse("/move/second/child", HttpMethod.POST, request);
        assertThat(method, is(VRaptorMatchers.resourceMethod(Clients.class.getMethod("move"))));
        mockery.assertIsSatisfied();
    }

    @Test
    public void returnsNullIfMethodNotFound() {
        ResourceMethod method = router.parse("/projects", HttpMethod.POST, request);
        assertThat(method, is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void returnsNullIfMethodIsNotPublic() {
        ResourceMethod method = router.parse("/protectMe", HttpMethod.POST, request);
        assertThat(method, is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void returnsNullIfMethodIsStatic() {
        ResourceMethod method = router.parse("/staticMe", HttpMethod.POST, request);
        assertThat(method, is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @br.com.caelum.vraptor.Resource
    public static class Clients {
        @Path("/move/*/child")
        public void move() {
        }

        @Path("/clients")
        public void list() {
        }

        @Path("/clients/remove")
        @Delete
        public void remove() {
        }

        @Path("/clients/head")
        @Head
        public void head() {
        }

        public void add() {
        }

        @Path("/protectMe")
        protected void protectMe() {
        }

        @Path("/staticMe")
        public static void staticMe() {
        }

        public void toInherit() {
        }
    }

    @Test
    public void shouldFindNonAnnotatedNonStaticPublicMethodWithComponentNameInVariableCamelCaseConventionAsURI()
            throws Exception {
        ResourceMethod method = router.parse("/clients/add", HttpMethod.POST, request);
        assertThat(method, is(VRaptorMatchers.resourceMethod(Clients.class.getMethod("add"))));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldIgnoreAResourceWithTheWrongWebMethod() throws SecurityException {
        ResourceMethod method = router.parse("/clients/remove", HttpMethod.POST, request);
        assertThat(method, is(Matchers.nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldAcceptAResultWithASpecificWebMethod() throws SecurityException, NoSuchMethodException {
        ResourceMethod method = router.parse("/clients/head", HttpMethod.HEAD, request);
        assertThat(method, is(VRaptorMatchers.resourceMethod(Clients.class.getMethod("head"))));
        mockery.assertIsSatisfied();
    }

    static class NiceClients extends Clients {
    }

    @Test
    public void findsInheritedMethodsWithDefaultNames() throws SecurityException, NoSuchMethodException {
        ResourceClass childResource = mockery.resource(NiceClients.class);
        router.register(childResource);
        ResourceMethod method = router.parse("/niceClients/toInherit", HttpMethod.POST, request);
        assertThat(method, is(VRaptorMatchers.resourceMethod(Clients.class.getMethod("toInherit"))));
        mockery.assertIsSatisfied();
    }

}

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
package br.com.caelum.vraptor.ioc.pico;

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
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.resource.DefaultResourceAndMethodLookup;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultResourceAndMethodLookupTest {

    private VRaptorMockery mockery;
    private DefaultResourceAndMethodLookup lookuper;
    private Resource resource;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.resource = mockery.resource(Clients.class);
        this.lookuper = new DefaultResourceAndMethodLookup(resource);
    }
    
    @Test
    public void matchesWhenUsingAWildcard() throws SecurityException, NoSuchMethodException {
        ResourceMethod method = lookuper.methodFor("/move/second/child", HttpMethod.POST);
        assertThat(method, is(VRaptorMatchers.resourceMethod(Clients.class.getMethod("move"))));
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void findsTheCorrectAnnotatedMethodIfThereIsNoWebMethodAnnotationPresent() throws SecurityException, NoSuchMethodException {
        ResourceMethod method = lookuper.methodFor("/clients", HttpMethod.POST);
        assertThat(method.getMethod(), is(equalTo(Clients.class.getMethod("list"))));
        mockery.assertIsSatisfied();
    }

    @Test
    public void returnsNullIfMethodNotFound() {
        ResourceMethod method = lookuper.methodFor("/projects", HttpMethod.POST);
        assertThat(method, is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void returnsNullIfMethodIsNotPublic() {
        ResourceMethod method = lookuper.methodFor("/protectMe", HttpMethod.POST);
        assertThat(method, is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void returnsNullIfMethodIsStatic() {
        ResourceMethod method = lookuper.methodFor("/staticMe", HttpMethod.POST);
        assertThat(method, is(nullValue()));
        mockery.assertIsSatisfied();
    }

    static class Clients {
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
    public void shouldFindAPublicNonStaticNonAnnotatedMethodWithTheSameNameAsTheGivenId() throws SecurityException, NoSuchMethodException {
        ResourceMethod method = lookuper.methodFor("/Clients/add", HttpMethod.POST);
        assertThat(method, is(VRaptorMatchers.resourceMethod(Clients.class.getMethod("add"))));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldIgnoreAResourceWithTheWrongWebMethod() throws SecurityException {
        ResourceMethod method = lookuper.methodFor("/clients/remove", HttpMethod.POST);
        assertThat(method, is(Matchers.nullValue()));
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void shouldAcceptAResultWithASpecificWebMethod() throws SecurityException, NoSuchMethodException {
        ResourceMethod method = lookuper.methodFor("/clients/head", HttpMethod.HEAD);
        assertThat(method, is(VRaptorMatchers.resourceMethod(Clients.class.getMethod("head"))));
        mockery.assertIsSatisfied();
    }

    static class NiceClients extends Clients {
    }

    public void findsInheritedMethodsWithDefaultNames() throws SecurityException, NoSuchMethodException {
        this.resource = mockery.resource(NiceClients.class);
        this.lookuper = new DefaultResourceAndMethodLookup(resource);
        ResourceMethod method = lookuper.methodFor("/NiceClients/toInherit", HttpMethod.POST);
        assertThat(method, is(VRaptorMatchers.resourceMethod(Clients.class.getMethod("toInherit"))));
        mockery.assertIsSatisfied();
    }

}

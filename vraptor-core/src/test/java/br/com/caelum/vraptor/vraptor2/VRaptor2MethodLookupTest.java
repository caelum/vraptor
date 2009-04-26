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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;
import org.vraptor.annotations.Component;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.resource.DefaultResourceAndMethodLookup;
import br.com.caelum.vraptor.resource.Resource;

public class VRaptor2MethodLookupTest {

    private VRaptorMockery mockery;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
    }

    class NonVRaptorComponent {
        public void name() {
        }
    }

    @Test
    public void shouldUseVRaptor3AlgorithmIfNotAVRaptor2Component() {
        final Resource resource = mockery.resource(NonVRaptorComponent.class);
        VRaptor2MethodLookup lookup = new VRaptor2MethodLookup(resource);
        assertThat(lookup.methodFor("id", "name"), is(equalTo(new DefaultResourceAndMethodLookup(resource).methodFor(
                "id", "name"))));
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
    }

    @Test
    public void ignoresNonPublicMethod() {
        final Resource resource = mockery.resource(MyResource.class);
        VRaptor2MethodLookup lookup = new VRaptor2MethodLookup(resource);
        assertThat(lookup.methodFor("/MyResource.ignorableStatic.logic", "ignorableStatic"), is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void ignoresStaticMethod() {
        final Resource resource = mockery.resource(MyResource.class);
        VRaptor2MethodLookup lookup = new VRaptor2MethodLookup(resource);
        assertThat(lookup.methodFor("/MyResource.ignorableProtected.logic", "ignorableProtected"), is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void returnsNullIfNothingFound() {
        final Resource resource = mockery.resource(MyResource.class);
        VRaptor2MethodLookup lookup = new VRaptor2MethodLookup(resource);
        assertThat(lookup.methodFor("/MyResource.unfindable.logic", "unfindable"), is(nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void returnsTheCorrectDefaultResourceMethodIfFound() throws SecurityException, NoSuchMethodException {
        final Resource resource = mockery.resource(MyResource.class);
        VRaptor2MethodLookup lookup = new VRaptor2MethodLookup(resource);
        assertThat(lookup.methodFor("/MyResource.findable.logic", "findable"), is(VRaptorMatchers.resourceMethod(MyResource.class.getMethod("findable"))));
        mockery.assertIsSatisfied();
    }

}

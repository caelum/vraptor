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

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.resource.DefaultResourceRegistry;
import br.com.caelum.vraptor.resource.MethodLookupBuilder;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceAndMethodLookup;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.VRaptorInfo;

public class DefaultResourceRegistryTest {

    private Mockery mockery;
    private DefaultResourceRegistry registry;
    private MethodLookupBuilder builder;
    private ResourceAndMethodLookup methodLookup;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.builder = mockery.mock(MethodLookupBuilder.class);
        this.methodLookup = mockery.mock(ResourceAndMethodLookup.class);
        mockery.checking(new Expectations() {
            {
                one(builder).lookupFor(with(VRaptorMatchers.resource(VRaptorInfo.class)));
                will(returnValue(methodLookup));
            }
        });
        this.registry = new DefaultResourceRegistry(builder);
    }

    @Test
    public void testReturnsResourceIfFound() throws SecurityException {
        final ResourceMethod method = mockery.mock(ResourceMethod.class);
        final Resource resource = mockery.mock(Resource.class);
        mockery.checking(new Expectations() {
            {
                one(builder).lookupFor(resource);
                will(returnValue(methodLookup));
                one(methodLookup).methodFor("/clients", "POST");
                will(returnValue(method));
            }
        });
        registry.register(resource);
        assertThat(registry.gimmeThis("/clients", "POST"), is(equalTo(method)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void testReturnsNullIfResourceNotFound() {
        mockery.checking(new Expectations() {
            {
                one(methodLookup).methodFor("unknown_id", "POST");
                will(returnValue(null));
            }
        });
        ResourceMethod method = registry.gimmeThis("unknown_id", "POST");
        assertThat(method, is(Matchers.nullValue()));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldRegisterVRaptorInfoByDefault() throws SecurityException {
        final ResourceMethod method = mockery.mock(ResourceMethod.class);
        mockery.checking(new Expectations() {
            {
                one(methodLookup).methodFor("/is_using_vraptor", "GET");
                will(returnValue(method));
            }
        });
        assertThat(registry.gimmeThis("/is_using_vraptor", "GET"), is(equalTo(method)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldAddAllResourcesToACommonList() {
        final Resource myResource = mockery.mock(Resource.class);
        mockery.checking(new Expectations() {
            {
                one(builder).lookupFor(myResource);
                will(returnValue(null));
            }
        });
        registry.register(myResource);
        assertThat(registry.all(), Matchers.hasItem(myResource));
        mockery.assertIsSatisfied();
    }
    
}

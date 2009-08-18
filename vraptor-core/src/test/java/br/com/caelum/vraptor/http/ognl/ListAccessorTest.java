/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
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
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.http.ognl;

import java.util.ArrayList;
import java.util.List;

import ognl.OgnlContext;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class ListAccessorTest {

    private ListAccessor accessor;
    private VRaptorMockery mockery;
    private OgnlContext context;
    private Container container;
    private List<String> instance;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery(true);
        this.context = mockery.mock(OgnlContext.class);
        this.container = mockery.mock(Container.class);
        this.instance = new ArrayList<String>();
        mockery.checking(new Expectations() {
            {
                allowing(context).getRoot();
                will(returnValue(instance));
                one(context).get(Container.class);
                will(returnValue(container));
            }
        });
        this.accessor = new ListAccessor();
    }

    @Test
    public void gettingShouldReturnNullIfIndexNotFound() throws Exception {
        Object value = accessor.getProperty(null, instance, 1);
        MatcherAssert.assertThat(value, Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void gettingShouldReturnValueIfIndexFound() throws Exception {
        instance.add("nothing");
        instance.add("guilherme");
        Object value = accessor.getProperty(null, instance, 1);
        MatcherAssert.assertThat(value, Matchers.is(Matchers.equalTo((Object) "guilherme")));
    }

    @Test
    public void settingShouldNullifyUpToIndex() throws Exception {
        accessor.setProperty(context, instance, 1, "hello");
        MatcherAssert.assertThat(instance.get(0), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(instance.get(1), Matchers.is(Matchers.equalTo("hello")));
    }

}

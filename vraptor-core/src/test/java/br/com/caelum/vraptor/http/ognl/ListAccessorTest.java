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

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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.Member;
import java.util.Map;

import ognl.Evaluation;
import ognl.OgnlContext;
import ognl.SimpleNode;
import ognl.TypeConverter;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.proxy.CglibProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.ReflectionInstanceCreator;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class ArrayAccessorTest {

    private ArrayAccessor accessor;
    private VRaptorMockery mockery;
    private OgnlContext context;
    private Evaluation evaluation;
    private SimpleNode node;
    private Data instance;
    private TypeConverter typeConverter;

    class Data {
        private Long[] simpleNode;

        public void setSimpleNode(Long[] simpleNode) {
            this.simpleNode = simpleNode;
        }

        public Long[] getSimpleNode() {
            return simpleNode;
        }
    }

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery(true);
        this.context = mockery.mock(OgnlContext.class);
        this.evaluation = mockery.mock(Evaluation.class);
        this.node = mockery.mock(SimpleNode.class);
        this.instance = new Data();
        this.typeConverter = mockery.mock(TypeConverter.class);
        final EmptyElementsRemoval removal = new EmptyElementsRemoval() {
            @Override
			public void removeExtraElements() {
                // does nothing
            }
        };
        final Proxifier proxifier = new CglibProxifier(new ReflectionInstanceCreator());
        accessor = new ArrayAccessor();
        mockery.checking(new Expectations() {
            {
            	allowing(context).get("removal"); will(returnValue(removal));
            	allowing(context).get("proxifier"); will(returnValue(proxifier));
                allowing(context).getCurrentEvaluation();
                will(returnValue(evaluation));
                allowing(evaluation).getPrevious();
                will(returnValue(evaluation));
                allowing(evaluation).getNode();
                will(returnValue(node));
                one(node).toString();
                will(returnValue("values"));
                allowing(evaluation).getSource();
                will(returnValue(instance));
                one(context).getTypeConverter();
                will(returnValue(typeConverter));
            }
        });
    }

    @Test
    public void gettingShouldReturnNullIfIndexNotFound() throws Exception {
        Long[] l = new Long[] {};
        Object value = accessor.getProperty(null, l, 1);
        assertThat(value, is(nullValue()));
    }

    @Test
    public void gettingShouldReturnValueIfIndexFound() throws Exception {
        Long[] l = new Long[] { 15L, 22L };
        Object value = accessor.getProperty(null, l, 1);
        assertThat(value, is(equalTo((Object) 22L)));
    }

    @Test
    public void settingShouldNullifyUpToIndexAndIgnoreRemoval() throws Exception {
        final Long[] l = new Long[] {};
        mockery.checking(new Expectations() {
            {
            	allowing(context).getRoot();
                one(typeConverter).convertValue((Map) with(anything()), with(anything()),
                        (Member) with(anything()), (String) with(an(String.class)), with(anything()),
                        (Class) with(an(Class.class)));
                will(returnValue(22L));
            }
        });
        accessor.setProperty(context, l, 1, 22L);
        assertThat(instance.simpleNode[0], is(nullValue()));
        assertThat(instance.simpleNode[1], is(equalTo(22L)));
    }

}

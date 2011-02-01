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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import ognl.Ognl;
import ognl.OgnlException;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class VRaptorConvertersAdapterTest {

    private VRaptorMockery mockery;
    private Converters converters;
    private VRaptorConvertersAdapter adapter;
    private Cat myCat;
	private Converter converter;
	private ResourceBundle bundle;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.converters = mockery.mock(Converters.class);
        this.bundle = ResourceBundle.getBundle("messages");
        this.adapter = new VRaptorConvertersAdapter(converters, bundle);
        this.converter = mockery.mock(Converter.class);
        this.myCat = new Cat();
    }

    public static class Cat {
        private int length;
        private Tail tail;
        private List<Leg> legs = new ArrayList<Leg>();

        public void setLength(int length) {
            this.length = length;
        }

        public int getLength() {
            return length;
        }

        public void setTail(Tail tail) {
            this.tail = tail;
        }

        public Tail getTail() {
            return tail;
        }

        public void setLegs(List<Leg> legs) {
            this.legs = legs;
        }

        public List<Leg> getLegs() {
            return legs;
        }

    }

    public static class Leg {
        @SuppressWarnings("unused")
        private final int length;

        public Leg(int length) {
            this.length = length;
        }
    }

    public static class Tail {
        public Tail(int l) {
            length = l;
        }

        private final int length;
    }

	@Test
	@SuppressWarnings("unchecked")
    public void shouldInvokePrimitiveConverter() throws OgnlException {
        mockery.checking(new Expectations() {
            {
                one(converters).to(int.class);
                will(returnValue(converter));
                one(converter).convert("2", int.class, bundle);
                will(returnValue(2));
            }
        });
        Map<?,?> context = Ognl.createDefaultContext(myCat);
        Ognl.setTypeConverter(context, adapter);
        Ognl.setValue("length", context, myCat, "2");
        assertThat(myCat.length, is(equalTo(2)));
        mockery.assertIsSatisfied();
    }

    @SuppressWarnings("unchecked")
	@Test
    public void shouldInvokeCustomTypeConverter() throws OgnlException {
        mockery.checking(new Expectations() {
            {
                one(converters).to(Tail.class);
                will(returnValue(converter));
                one(converter).convert("15", Tail.class, bundle);
                will(returnValue(new Tail(15)));
            }
        });
        Map<?,?> context = Ognl.createDefaultContext(myCat);
        Ognl.setTypeConverter(context, adapter);
        Ognl.setValue("tail", context, myCat, "15");
        assertThat(myCat.tail.length, is(equalTo(15)));
        mockery.assertIsSatisfied();
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoConverterIsFound() throws Throwable {
        mockery.checking(new Expectations() {
            {
                one(converters).to(Tail.class);
                will(returnValue(null));
            }
        });
        Map context = Ognl.createDefaultContext(myCat);
        Ognl.setTypeConverter(context, adapter);
        try {
            Ognl.setValue("tail", context, myCat, "15");
        } catch (OgnlException e) {
            mockery.assertIsSatisfied();
            throw e.getCause();
        }
    }

}

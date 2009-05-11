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
import br.com.caelum.vraptor.http.ognl.VRaptorConvertersAdapter;
import br.com.caelum.vraptor.test.VRaptorMockery;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.validator.Message;

public class VRaptorConvertersAdapterTest {

    private VRaptorMockery mockery;
    private Converters converters;
    private VRaptorConvertersAdapter adapter;
    private Cat myCat;
    @SuppressWarnings("unchecked")
	private Converter converter;
	private ArrayList<Message> errors;
	private ResourceBundle bundle;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.converters = mockery.mock(Converters.class);
        this.errors = new ArrayList<Message>();
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
        private int length;

        public Leg(int length) {
            this.length = length;
        }
    }

    public static class Tail {
        public Tail(int l) {
            length = l;
        }

        private int length;
    }

    @Test
    public void shouldInvokePrimitiveConverter() throws OgnlException {
        mockery.checking(new Expectations() {
            {
                one(converters).to(int.class, null);
                will(returnValue(converter));
                one(converter).convert("2", int.class, bundle);
                will(returnValue(2));
            }
        });
        Map context = Ognl.createDefaultContext(myCat);
        Ognl.setTypeConverter(context, adapter);
        Ognl.setValue("length", context, myCat, "2");
        assertThat(myCat.length, is(equalTo(2)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldInvokeCustomTypeConverter() throws OgnlException {
        mockery.checking(new Expectations() {
            {
                one(converters).to(Tail.class, null);
                will(returnValue(converter));
                one(converter).convert("15", Tail.class, bundle);
                will(returnValue(new Tail(15)));
            }
        });
        Map context = Ognl.createDefaultContext(myCat);
        Ognl.setTypeConverter(context, adapter);
        Ognl.setValue("tail", context, myCat, "15");
        assertThat(myCat.tail.length, is(equalTo(15)));
        mockery.assertIsSatisfied();
    }
    
    @SuppressWarnings("unchecked")
    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoConverterIsFound() throws Throwable {
        mockery.checking(new Expectations() {
            {
                one(converters).to(Tail.class, null);
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

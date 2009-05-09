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
package br.com.caelum.vraptor.converter;

import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.ognl.ArrayAccessor;
import br.com.caelum.vraptor.http.ognl.EmptyElementsRemoval;
import br.com.caelum.vraptor.http.ognl.ListAccessor;
import br.com.caelum.vraptor.http.ognl.ReflectionBasedNullHandler;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.validator.Message;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Unfortunately OGNL sucks so bad in its design that we had to create a "unit"
 * test which accesses more than a single class to test the ognl funcionality.
 * Even worse, OGNL sucks with its static configuration methods in such a way
 * that tests are not thread safe. Summing up: OGNL api sucks, OGNL idea rulez.
 * This test is here to ensure generic support through our implementation using
 * OGNL.
 *
 * @author Guilherme Silveira
 */
public class OgnlGenericTypesSupportTest {

    private Mockery mockery;
    private Cat myCat;
    private Converters converters;
    private OgnlContext context;
    private Container container;
    private EmptyElementsRemoval removal;
    private ResourceBundle bundle;
    private ArrayList<Message> errors;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.converters = mockery.mock(Converters.class);
        this.container = mockery.mock(Container.class);
        this.removal = new EmptyElementsRemoval();
        this.bundle = ResourceBundle.getBundle("messages");
        this.errors = new ArrayList<Message>();
        mockery.checking(new Expectations() {
            {
                allowing(container).instanceFor(Converters.class);
                will(returnValue(converters));
                allowing(converters).to(Long.class, container);
                will(returnValue(new LongConverter()));
                allowing(container).instanceFor(EmptyElementsRemoval.class);
                will(returnValue(removal));
            }
        });
        this.myCat = new Cat();
        OgnlRuntime.setNullHandler(Object.class, new ReflectionBasedNullHandler());
        OgnlRuntime.setPropertyAccessor(List.class, new ListAccessor());
        OgnlRuntime.setPropertyAccessor(Object[].class, new ArrayAccessor());
        this.context = (OgnlContext) Ognl.createDefaultContext(myCat);
        context.setTraceEvaluations(true);
        context.put(Container.class, container);
        // OgnlRuntime.setPropertyAccessor(Set.class, new SetAccessor());
        // OgnlRuntime.setPropertyAccessor(Map.class, new MapAccessor());
        Ognl.setTypeConverter(context, new OgnlToConvertersController(converters, errors, bundle));
    }

    public static class Cat {
        private List<String> legLength;

        public void setLegLength(List<String> legLength) {
            this.legLength = legLength;
        }

        public List<String> getLegLength() {
            return legLength;
        }

        public void setLegs(List<Leg> legs) {
            this.legs = legs;
        }

        public List<Leg> getLegs() {
            return legs;
        }

        public void setIds(Long[] ids) {
            this.ids = ids;
        }

        public Long[] getIds() {
            return ids;
        }

        public void setEyeColorCode(List<Long> eyeColorCode) {
            this.eyeColorCode = eyeColorCode;
        }

        public List<Long> getEyeColorCode() {
            return eyeColorCode;
        }

        private List<Leg> legs;
        private Long[] ids;
        private List<Long> eyeColorCode;
    }

    public static class Leg {
        private String color;

        public void setColor(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

    @Test
    public void canInstantiatingStringsInAListSettingItsInternalValueWithoutInvokingConverters() throws OgnlException {
        Ognl.setValue("legLength[0]", context, myCat, "small");
        List<String> legs = myCat.legLength;
        assertThat(legs.get(0), is(equalTo("small")));
        Ognl.setValue("legLength[1]", context, myCat, "big");
        assertThat(legs.get(1), is(equalTo("big")));
        mockery.assertIsSatisfied();
    }

    @Test
    public void canInstantiateAndPopulateAnArrayOfWrappers() throws OgnlException {
        Ognl.setValue("ids[0]", context, myCat, "3");
        assertThat(myCat.ids[0], is(equalTo(3L)));
        Ognl.setValue("ids[1]", context, myCat, "5");
        assertThat(myCat.ids[1], is(equalTo(5L)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void canInstantiateAndPopulateAListOfWrappers() throws OgnlException {
        Ognl.setValue("eyeColorCode[0]", context, myCat, "3");
        assertThat(myCat.eyeColorCode.get(0), is(equalTo(3L)));
        Ognl.setValue("eyeColorCode[1]", context, myCat, "5");
        assertThat(myCat.eyeColorCode.get(1), is(equalTo(5L)));
        mockery.assertIsSatisfied();
    }

}

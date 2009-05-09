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

import br.com.caelum.vraptor.converter.IntegerConverter;
import br.com.caelum.vraptor.converter.LocaleBasedCalendarConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.ognl.ArrayAccessor;
import br.com.caelum.vraptor.http.ognl.ListAccessor;
import br.com.caelum.vraptor.http.ognl.ReflectionBasedNullHandler;
import br.com.caelum.vraptor.http.ognl.VRaptorConvertersAdapter;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Unfortunately OGNL sucks so bad in its design that we had to create a "unit"
 * test which accesses more than a single class to test the ognl funcionality.
 * Even worse, OGNL sucks with its static configuration methods in such a way
 * that tests are not thread safe. Summing up: OGNL api sucks, OGNL idea rulez.
 * Tests written here are "acceptance tests" for the Ognl support on http
 * parameters.
 *
 * @author Guilherme Silveira
 */
public class MiscOgnlSupportTest {

    private Mockery mockery;
    private Converters converters;
    private OgnlContext context;
    private House house;
    private ResourceBundle bundle;
    private ArrayList<Message> errors;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.converters = mockery.mock(Converters.class);
        this.house = new House();
        OgnlRuntime.setNullHandler(Object.class, new ReflectionBasedNullHandler());
        OgnlRuntime.setPropertyAccessor(List.class, new ListAccessor());
        OgnlRuntime.setPropertyAccessor(Object[].class, new ArrayAccessor());
        this.context = (OgnlContext) Ognl.createDefaultContext(house);
        context.setTraceEvaluations(true);
        // OgnlRuntime.setPropertyAccessor(Set.class, new SetAccessor());
        // OgnlRuntime.setPropertyAccessor(Map.class, new MapAccessor());
        this.bundle = ResourceBundle.getBundle("messages");
        this.errors = new ArrayList<Message>();
        Ognl.setTypeConverter(context, new VRaptorConvertersAdapter(converters, bundle));
    }

    public static class Cat {
        private Leg firstLeg;

        public void setFirstLeg(Leg firstLeg) {
            this.firstLeg = firstLeg;
        }

        public Leg getFirstLeg() {
            return firstLeg;
        }
    }

    public static class Leg {
        private Integer id;
        private Calendar birthDay; // weird leg birthday!!

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setBirthDay(Calendar birthDay) {
            this.birthDay = birthDay;
        }

        public Calendar getBirthDay() {
            return birthDay;
        }
    }

    public static class House {
        private Cat cat;

        public void setCat(Cat cat) {
            this.cat = cat;
        }

        public Cat getCat() {
            return cat;
        }

    }

    @Test
    public void isCapableOfDealingWithEmptyParameterForInternalWrapperValue() throws OgnlException {
        mockery.checking(new Expectations() {{
            one(converters).to(Integer.class, null);
            will(returnValue(new IntegerConverter()));
        }});
        Ognl.setValue("cat.firstLeg.id", context, house, "");
        assertThat(house.cat.firstLeg.id, is(equalTo(null)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void isCapableOfDealingWithEmptyParameterForInternalValueWhichNeedsAConverter() throws OgnlException {
        final MutableRequest request = mockery.mock(MutableRequest.class);
        final RequestInfo webRequest = new RequestInfo(null, request, null);
        mockery.checking(new Expectations() {{
            exactly(2).of(request).getAttribute("javax.servlet.jsp.jstl.fmt.locale.request");
            will(returnValue("pt_br"));
            one(converters).to(Calendar.class, null);
            will(returnValue(new LocaleBasedCalendarConverter(webRequest)));
        }});
        Ognl.setValue("cat.firstLeg.birthDay", context, house, "10/5/2010");
        assertThat(house.cat.firstLeg.birthDay, is(equalTo((Calendar) new GregorianCalendar(2010, 4, 10))));
        mockery.assertIsSatisfied();
    }

}

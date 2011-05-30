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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.converter.IntegerConverter;
import br.com.caelum.vraptor.converter.LocaleBasedCalendarConverter;
import br.com.caelum.vraptor.converter.LongConverter;
import br.com.caelum.vraptor.converter.StringConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.JstlLocalization;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.proxy.CglibProxifier;
import br.com.caelum.vraptor.proxy.ReflectionInstanceCreator;

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

    private OgnlContext context;
    private House house;
    private ResourceBundle bundle;
	private @Mock Converters converters;
	private @Mock EmptyElementsRemoval removal;

	@Before
    public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		AbstractOgnlTestSupport.configOgnl(converters);
        this.house = new House();
        this.context = (OgnlContext) Ognl.createDefaultContext(house);
        context.setTraceEvaluations(true);
        context.put("nullHandler", new GenericNullHandler(removal));
        // OgnlRuntime.setPropertyAccessor(Set.class, new SetAccessor());
        // OgnlRuntime.setPropertyAccessor(Map.class, new MapAccessor());
        this.bundle = ResourceBundle.getBundle("messages");
        Ognl.setTypeConverter(context, new VRaptorConvertersAdapter(converters, bundle));
        when(converters.to(String.class)).thenReturn(new StringConverter());
		when(converters.to(Long.class)).thenReturn(new LongConverter());
		
		context.put("proxifier", new CglibProxifier(new ReflectionInstanceCreator()));
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
        when(converters.to(Integer.class)).thenReturn(new IntegerConverter());
        Ognl.setValue("cat.firstLeg.id", context, house, "");
        assertThat(house.cat.firstLeg.id, is(equalTo(null)));
    }

    @Test
    public void isCapableOfDealingWithEmptyParameterForInternalValueWhichNeedsAConverter() throws OgnlException {
        final MutableRequest request = mock(MutableRequest.class);
        final RequestInfo webRequest = new RequestInfo(null, null, request, null);
        final JstlLocalization jstlLocalization = new JstlLocalization(webRequest);

        when(request.getAttribute("javax.servlet.jsp.jstl.fmt.locale.request")).thenReturn("pt_br");
        when(converters.to(Calendar.class)).thenReturn(new LocaleBasedCalendarConverter(jstlLocalization));
        Ognl.setValue("cat.firstLeg.birthDay", context, house, "10/5/2010");
        assertThat(house.cat.firstLeg.birthDay, is(equalTo((Calendar) new GregorianCalendar(2010, 4, 10))));
    }

}

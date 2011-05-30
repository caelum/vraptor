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

import java.util.List;
import java.util.ResourceBundle;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.converter.LongConverter;
import br.com.caelum.vraptor.converter.StringConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.proxy.CglibProxifier;
import br.com.caelum.vraptor.proxy.ReflectionInstanceCreator;

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

    private Cat myCat;
    private OgnlContext context;
    private Container container;
    private EmptyElementsRemoval removal;
    private ResourceBundle bundle;
	private Mockery mockery;
	private Converters converters;

    @Before
    public void setup() throws Exception {
    	mockery = new Mockery();
    	converters = mockery.mock(Converters.class);
    	AbstractOgnlTestSupport.configOgnl(converters);

        this.container = mockery.mock(Container.class);
        this.removal = new EmptyElementsRemoval();
        this.bundle = ResourceBundle.getBundle("messages");
        mockery.checking(new Expectations() {
            {
                allowing(container).instanceFor(Converters.class);
                will(returnValue(converters));
                allowing(converters).to(Long.class);
                will(returnValue(new LongConverter()));
                allowing(converters).to(String.class);
                will(returnValue(new StringConverter()));
                allowing(container).instanceFor(EmptyElementsRemoval.class);
                will(returnValue(removal));
            }
        });
        this.myCat = new Cat();
        this.context = (OgnlContext) Ognl.createDefaultContext(myCat);
        context.setTraceEvaluations(true);
        context.put(Container.class, container);
        context.put("removal", removal);
        context.put("nullHandler", new GenericNullHandler(removal));
        // OgnlRuntime.setPropertyAccessor(Set.class, new SetAccessor());
        // OgnlRuntime.setPropertyAccessor(Map.class, new MapAccessor());
        Ognl.setTypeConverter(context, new VRaptorConvertersAdapter(converters, bundle));
        
        context.put("proxifier", new CglibProxifier(new ReflectionInstanceCreator()));
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

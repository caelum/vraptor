package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.List;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.EmptyElementsRemoval;
import br.com.caelum.vraptor.http.ognl.ArrayAccessor;
import br.com.caelum.vraptor.http.ognl.ListAccessor;
import br.com.caelum.vraptor.http.ognl.ReflectionBasedNullHandler;
import br.com.caelum.vraptor.ioc.Container;

/**
 * Unfortunately OGNL sucks so bad in its design that we had to create a "unit"
 * test which accesses more than a single class to test the ognl funcionality.
 * Even worse, OGNL sucks with its static configuration methods in such a way
 * that tests are not thread safe. Summing up: OGNL api sucks, OGNL idea rulez.
 * This test is here to ensure generic support through our implementation using
 * OGNL.
 * 
 * @author Guilherme Silveira
 * 
 */
public class OgnlGenericTypesSupportTest {

    private Mockery mockery;
    private Cat myCat;
    private Converters converters;
    private OgnlContext context;
    private Container container;
    private EmptyElementsRemoval removal;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.converters = mockery.mock(Converters.class);
        this.container = mockery.mock(Container.class);
        this.removal = new EmptyElementsRemoval();
        mockery.checking(new Expectations() {
            {
                allowing(container).instanceFor(Converters.class); will(returnValue(converters));
                allowing(converters).to(Long.class, container); will(returnValue(new LongConverter()));
                allowing(container).instanceFor(EmptyElementsRemoval.class); will(returnValue(removal));
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
        Ognl.setTypeConverter(context, new OgnlToConvertersController(converters));
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
    public void isCapableOfInstantiatingStringsInAListSettingItsInternalValueWithoutInvokingConverters() throws OgnlException {
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

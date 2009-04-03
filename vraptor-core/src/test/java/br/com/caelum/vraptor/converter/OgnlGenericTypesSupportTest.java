package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.List;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.ognl.ListAccessor;
import br.com.caelum.vraptor.http.ognl.ReflectionBasedNullHandler;

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

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.converters = mockery.mock(Converters.class);
        this.myCat = new Cat();
        OgnlRuntime.setNullHandler(Object.class, new ReflectionBasedNullHandler());
        OgnlRuntime.setPropertyAccessor(List.class, new ListAccessor());
        this.context = (OgnlContext) Ognl.createDefaultContext(myCat);
        context.setTraceEvaluations(true);
        // OgnlRuntime.setPropertyAccessor(Set.class, new SetAccessor());
        // OgnlRuntime.setPropertyAccessor(Array.class, new ArrayAccessor());
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
        private List<Leg> legs;
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
    public void isCapableOfInstantiatingCustomTypesInAListSettingItsInternalValue() throws OgnlException {
        Ognl.setValue("legs[0].color", context, myCat, "red");
        assertThat(myCat.legs.get(0).color, is(equalTo("red")));
    }

    @Test
    public void isCapableOfInstantiatingStringsInAListSettingItsInternalValue() throws OgnlException {
        Ognl.setValue("legLength[0]", context, myCat, "small");
        List<String> legs = myCat.legLength;
        assertThat(legs.get(0), is(equalTo("small")));
        Ognl.setValue("legLength[1]", context, myCat, "big");
        assertThat(legs.get(1), is(equalTo("big")));
    }


}

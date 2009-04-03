package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ognl.NullHandler;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.core.Converters;

public class OgnlBasedConverterControllerTest {

    private VRaptorMockery mockery;
    private Converters converters;
    private OgnlBasedConverterController controller;
    private Cat myCat;
    private Converter converter;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.converters = mockery.mock(Converters.class);
        this.controller = new OgnlBasedConverterController(converters);
        this.converter = mockery.mock(Converter.class);
        this.myCat = new Cat();
    }

    public static class Cat {
        private int length;
        private Tail tail;
        private List<Leg> legs;

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
                one(converters).to(int.class);
                will(returnValue(converter));
                one(converter).convert("2");
                will(returnValue(2));
            }
        });
        Map context = Ognl.createDefaultContext(myCat);
        Ognl.setTypeConverter(context, controller);
        Ognl.setValue("length", context, myCat, "2");
        assertThat(myCat.length, is(equalTo(2)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldInvokeCustomTypeConverter() throws OgnlException {
        mockery.checking(new Expectations() {
            {
                one(converters).to(Tail.class);
                will(returnValue(converter));
                one(converter).convert("15");
                will(returnValue(new Tail(15)));
            }
        });
        Map context = Ognl.createDefaultContext(myCat);
        Ognl.setTypeConverter(context, controller);
        Ognl.setValue("tail", context, myCat, "15");
        assertThat(myCat.tail.length, is(equalTo(15)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldInvokeCustomTypeConverterForGenericCollectionOfTypeConverter() throws OgnlException {
        mockery.checking(new Expectations() {
            {
                one(converters).to(Tail.class);
                will(returnValue(converter));
                one(converter).convert("15");
                will(returnValue(new Tail(15)));
            }
        });
        OgnlRuntime.setNullHandler(Object.class, new NullHandler() {

            public Object nullMethodResult(Map arg0, Object arg1, String arg2, Object[] arg3) {
                return null;
            }

            public Object nullPropertyValue(Map context, Object target, Object property) {

                OgnlContext ctx = (OgnlContext) context;
                int indexInParent = ctx.getCurrentEvaluation().getNode().getIndexInParent();
                int maxIndex = ctx.getRootEvaluation().getNode().jjtGetNumChildren() - 1;
                if ( (indexInParent != -1 && indexInParent < maxIndex)) {
                    // insantiate
                }
                if(target instanceof Cat) {
                    return new ArrayList();
                }
                return null;
            }

        });
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(myCat);
        context.setTraceEvaluations(true);
        Ognl.setTypeConverter(context, controller);
        Ognl.setValue("legs[0]", context, myCat, "15");
        assertThat(myCat.getLegs().get(0).length, is(equalTo(15)));
        Ognl.setValue("legs[1]", context, myCat, "11");
        assertThat(myCat.getLegs().get(1).length, is(equalTo(11)));
        mockery.assertIsSatisfied();
    }

}

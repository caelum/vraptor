package br.com.caelum.vraptor.core;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.ioc.Container;

public class DefaultConvertersTest {

    private VRaptorMockery mockery;
    private Container container;
    private DefaultConverters converters;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.container = mockery.mock(Container.class);
        this.converters = new DefaultConverters();
    }

    @Test(expected = IllegalArgumentException.class)
    public void complainsIfNoConverterFound() {
        converters.to(DefaultConvertersTest.class, container);
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertingANonAnnotatedConverterEndsUpComplaining() {
        converters.register(WrongConverter.class);
    }

    class WrongConverter implements Converter<String> {

        public String convert(String value, Class<? extends String> type) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    class MyData {
    }

    @Convert(MyData.class)
    class MyConverter implements Converter<MyData> {
        public MyData convert(String value, Class<? extends MyData> type) {
            return null;
        }
    }

    @Convert(MyData.class)
    class MySecondConverter implements Converter<MyData> {
        public MyData convert(String value, Class<? extends MyData> type) {
            return null;
        }
    }

    @Test
    public void registersAndUsesTheConverterInstaceForTheSpecifiedType() {
        converters.register(MyConverter.class);
        mockery.checking(new Expectations() {
            {
                one(container).instanceFor(MyConverter.class);
                will(returnValue(null));
                one(container).register(MyConverter.class);
                one(container).instanceFor(MyConverter.class);
                will(returnValue(new MyConverter()));
            }
        });
        Converter<?> found = converters.to(MyData.class, container);
        assertThat(found.getClass(), is(typeCompatibleWith(MyConverter.class)));
        mockery.assertIsSatisfied();
    }
    @Test
    public void askingTwiceForTheSameConverterWontRegisterItTwice() {
        converters.register(MyConverter.class);
        mockery.checking(new Expectations() {
            {
                one(container).instanceFor(MyConverter.class);
                will(returnValue(null));
                one(container).register(MyConverter.class);
                exactly(3).of(container).instanceFor(MyConverter.class);
                will(returnValue(new MyConverter()));
            }
        });
        Converter<?> found = converters.to(MyData.class, container);
        MatcherAssert.assertThat(found.getClass(), Matchers.is(Matchers.equalTo((Class)MyConverter.class)));
        found = converters.to(MyData.class, container);
        MatcherAssert.assertThat(found.getClass(), Matchers.is(Matchers.equalTo((Class)MyConverter.class)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void registersAndUsesTheLastConverterInstaceRegisteredForTheSpecifiedType() {
        converters.register(MyConverter.class);
        converters.register(MySecondConverter.class);
        mockery.checking(new Expectations() {
            {
                one(container).register(MySecondConverter.class);
                one(container).instanceFor(MySecondConverter.class);
                will(returnValue(null));
                one(container).instanceFor(MySecondConverter.class);
                will(returnValue(new MySecondConverter()));
            }
        });
        Converter<?> found = converters.to(MyData.class, container);
        assertThat(found.getClass(), is(typeCompatibleWith(MySecondConverter.class)));
        mockery.assertIsSatisfied();
    }

}

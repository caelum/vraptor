package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.VRaptorMockery;
import br.com.caelum.vraptor.converter.BooleanConverter;
import br.com.caelum.vraptor.converter.ByteConverter;
import br.com.caelum.vraptor.converter.DoubleConverter;
import br.com.caelum.vraptor.converter.EnumConverter;
import br.com.caelum.vraptor.converter.FloatConverter;
import br.com.caelum.vraptor.converter.IntegerConverter;
import br.com.caelum.vraptor.converter.LocaleBasedCalendarConverter;
import br.com.caelum.vraptor.converter.LocaleBasedDateConverter;
import br.com.caelum.vraptor.converter.LongConverter;
import br.com.caelum.vraptor.converter.PrimitiveBooleanConverter;
import br.com.caelum.vraptor.converter.PrimitiveByteConverter;
import br.com.caelum.vraptor.converter.PrimitiveDoubleConverter;
import br.com.caelum.vraptor.converter.PrimitiveFloatConverter;
import br.com.caelum.vraptor.converter.PrimitiveIntConverter;
import br.com.caelum.vraptor.converter.PrimitiveLongConverter;
import br.com.caelum.vraptor.converter.PrimitiveShortConverter;
import br.com.caelum.vraptor.converter.ShortConverter;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFileConverter;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.ioc.Container;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.hamcrest.Matchers.instanceOf;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class DefaultConvertersTest {

    private VRaptorMockery mockery;
    private Container container;
    private DefaultConverters converters;
    private ComponentRegistry componentRegistry;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery(true);
        this.container = mockery.mock(Container.class);
        this.componentRegistry = mockery.mock(ComponentRegistry.class);
        mockery.checking(new Expectations() {
            {
                allowing(componentRegistry).register((Class) with(an(Class.class)), (Class) with(an(Class.class)));
            }
        });
        this.converters = new DefaultConverters(componentRegistry);
    }

    @Test
    public void shouldRegisterConvertersForAllDefaultTypes() {
        final HashMap<Class, Class<? extends Converter>> EXPECTED_CONVERTERS = new HashMap<Class, Class<? extends Converter>>() {
            {
                put(int.class, PrimitiveIntConverter.class);
                put(long.class, PrimitiveLongConverter.class);
                put(short.class, PrimitiveShortConverter.class);
                put(byte.class, PrimitiveByteConverter.class);
                put(double.class, PrimitiveDoubleConverter.class);
                put(float.class, PrimitiveFloatConverter.class);
                put(boolean.class, PrimitiveBooleanConverter.class);
                put(Integer.class, IntegerConverter.class);
                put(Long.class, LongConverter.class);
                put(Short.class, ShortConverter.class);
                put(Byte.class, ByteConverter.class);
                put(Double.class, DoubleConverter.class);
                put(Float.class, FloatConverter.class);
                put(Boolean.class, BooleanConverter.class);
                put(Calendar.class, LocaleBasedCalendarConverter.class);
                put(Date.class, LocaleBasedDateConverter.class);
                put(Enum.class, EnumConverter.class);
                put(UploadedFile.class, UploadedFileConverter.class);
            }
        };

        mockery.checking(new Expectations() {
            {
                for (Class<? extends Converter> converterType : EXPECTED_CONVERTERS.values()) {
                    Converter expected = mockery.mock(converterType);
                    one(componentRegistry).register(converterType, converterType);
                    one(container).instanceFor(converterType);
                    will(returnValue(expected));
                }
            }
        });

        for (Map.Entry<Class, Class<? extends Converter>> entry : EXPECTED_CONVERTERS.entrySet()) {
            Class typeFor = entry.getKey();
            Class<? extends Converter> converterType = entry.getValue();
            Converter converter = converters.to(typeFor, container);
            assertThat(converter, is(instanceOf(converterType)));
        }
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
                will(returnValue(new MyConverter()));
            }
        });
        Converter<?> found = converters.to(MyData.class, container);
        assertThat(found.getClass(), is(typeCompatibleWith(MyConverter.class)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void usesTheLastConverterInstanceRegisteredForTheSpecifiedType() {
        converters.register(MyConverter.class);
        converters.register(MySecondConverter.class);
        mockery.checking(new Expectations() {
            {
                one(container).instanceFor(MySecondConverter.class);
                will(returnValue(new MySecondConverter()));
            }
        });
        Converter<?> found = converters.to(MyData.class, container);
        assertThat(found.getClass(), is(typeCompatibleWith(MySecondConverter.class)));
        mockery.assertIsSatisfied();
    }

}

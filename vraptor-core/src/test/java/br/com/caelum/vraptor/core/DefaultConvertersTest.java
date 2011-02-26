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
package br.com.caelum.vraptor.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.VRaptorException;
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
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFileConverter;
import br.com.caelum.vraptor.ioc.Container;

public class DefaultConvertersTest {

    @Mock private Container container;
    private DefaultConverters converters;

    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
        this.converters = new DefaultConverters(container);
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
            private static final long serialVersionUID = 8559316558416038474L;
        };

        for (Class<? extends Converter> converterType : EXPECTED_CONVERTERS.values()) {
            Converter<?> expected = mock(converterType);
            when((Converter)container.instanceFor(converterType)).thenReturn(expected);
        }

        for (Entry<Class, Class<? extends Converter>> entry : EXPECTED_CONVERTERS.entrySet()) {
            Class<?> typeFor = entry.getKey();
            Class<? extends Converter> converterType = entry.getValue();
            Converter<?> converter = converters.to(typeFor);
            assertThat(converter, is(instanceOf(converterType)));
        }
    }

    @Test(expected = VRaptorException.class)
    public void complainsIfNoConverterFound() {
        converters.to(DefaultConvertersTest.class);
    }

    @Test(expected = VRaptorException.class)
    public void convertingANonAnnotatedConverterEndsUpComplaining() {
        converters.register(WrongConverter.class);
    }

    class WrongConverter implements Converter<String> {

        public String convert(String value, Class<? extends String> type, ResourceBundle bundle) {
            return null;
        }
    }

    class MyData {
    }

    @Convert(MyData.class)
    class MyConverter implements Converter<MyData> {
        public MyData convert(String value, Class<? extends MyData> type, ResourceBundle bundle) {
            return null;
        }
    }

    @Convert(MyData.class)
    class MySecondConverter implements Converter<MyData> {
        public MyData convert(String value, Class<? extends MyData> type, ResourceBundle bundle) {
            return null;
        }
    }

    @Test
    public void registersAndUsesTheConverterInstaceForTheSpecifiedType() {
        converters.register(MyConverter.class);
        when(container.instanceFor(MyConverter.class)).thenReturn(new MyConverter());

        Converter<?> found = converters.to(MyData.class);
        assertThat(found.getClass(), is(typeCompatibleWith(MyConverter.class)));
    }

    @Test
    public void usesTheLastConverterInstanceRegisteredForTheSpecifiedType() {
        converters.register(MyConverter.class);
        converters.register(MySecondConverter.class);
        when(container.instanceFor(MySecondConverter.class)).thenReturn(new MySecondConverter());

        Converter<?> found = converters.to(MyData.class);
        assertThat(found.getClass(), is(typeCompatibleWith(MySecondConverter.class)));
    }

    @Test
	public void existsForWillReturnTrueForRegisteredConverters() throws Exception {
		converters.register(MyConverter.class);

		when(container.instanceFor(MyConverter.class)).thenReturn(new MyConverter());

		assertTrue(converters.existsFor(MyData.class));
	}

}

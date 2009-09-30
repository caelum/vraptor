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
package br.com.caelum.vraptor.http.asm;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class SignatureConverterTest {

    private SignatureConverter converter;

    @Before
    public void setup() {
        this.converter = new SignatureConverter();
    }

    @Test
    public void shouldBeAbleToConvertPrimitiveTypes() {
        Class<?>[] primitives = new Class[] { int.class, boolean.class, long.class, short.class, double.class,
                float.class, byte.class, char.class };
        for (Class<?> primitive : primitives) {
            MatcherAssert.assertThat(converter.extractTypeDefinition(primitive), Matchers.is(Matchers.equalTo(converter
                    .wrapperFor(primitive))));
        }
    }

    @Test
    public void shouldBeAbleToConvertArrayOfPrimitive() {
        MatcherAssert.assertThat(converter.extractTypeDefinition(int[].class), Matchers.is(Matchers.equalTo("[I")));
    }
    @Test
    public void shouldBeAbleToConvertArrayOfObjects() {
        MatcherAssert.assertThat(converter.extractTypeDefinition(String[].class), Matchers.is(Matchers.equalTo("[Ljava/lang/String;")));
    }
    @Test
    public void shouldBeAbleToConvertObject() {
        MatcherAssert.assertThat(converter.extractTypeDefinition(String.class), Matchers.is(Matchers.equalTo("Ljava/lang/String;")));
    }
    @Test
    public void shouldBeAbleToConvertGenericCollection() throws SecurityException, NoSuchMethodException {
        ParameterizedType type = (ParameterizedType)Cat.class.getDeclaredMethod("fightWith", List.class).getGenericParameterTypes()[0];
        MatcherAssert.assertThat(converter.extractTypeDefinition(type), Matchers.is(Matchers.equalTo("Ljava/util/List<Ljava/lang/String;>;")));
    }
    
    static public interface Cat {
        void fightWith(List<String> cats);
    }
}

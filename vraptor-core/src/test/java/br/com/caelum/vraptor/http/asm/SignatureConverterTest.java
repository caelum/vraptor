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

package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

public class PrimitiveBooleanConverterTest {
    
    private PrimitiveBooleanConverter converter;

    @Before
    public void setup() {
        this.converter = new PrimitiveBooleanConverter();
    }
    
    @Test
    public void shouldBeAbleToConvertNumbers(){
        assertThat((Boolean) converter.convert("", boolean.class), is(equalTo(false)));
        assertThat((Boolean) converter.convert("false", boolean.class), is(equalTo(false)));
        assertThat((Boolean) converter.convert("weird-stuff", boolean.class), is(equalTo(false)));
        assertThat((Boolean) converter.convert("true", boolean.class), is(equalTo(true)));
        assertThat((Boolean) converter.convert("True", boolean.class), is(equalTo(true)));
    }
    
    @Test
    public void shouldConvertToZeroWhenNull() {
    	assertThat((Boolean) converter.convert(null, boolean.class), is(equalTo(false)));
    }

    @Test
    public void shouldConvertToZeroWhenEmpty() {
    	assertThat((Boolean) converter.convert("", boolean.class), is(equalTo(false)));
    }

}

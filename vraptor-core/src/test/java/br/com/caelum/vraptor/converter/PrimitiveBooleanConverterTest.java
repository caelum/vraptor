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
        assertThat((Boolean) converter.convert(""), is(equalTo(false)));
        assertThat((Boolean) converter.convert("false"), is(equalTo(false)));
        assertThat((Boolean) converter.convert("weird-stuff"), is(equalTo(false)));
        assertThat((Boolean) converter.convert("true"), is(equalTo(true)));
        assertThat((Boolean) converter.convert("True"), is(equalTo(true)));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutNull() {
        converter.convert(null);
    }

}

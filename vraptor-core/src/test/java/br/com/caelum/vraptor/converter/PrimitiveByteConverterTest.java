package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

public class PrimitiveByteConverterTest {
    
    private PrimitiveByteConverter converter;

    @Before
    public void setup() {
        this.converter = new PrimitiveByteConverter();
    }
    
    @Test
    public void shouldBeAbleToConvertNumbers(){
        assertThat((Byte) converter.convert("7", byte.class), is(equalTo((byte)7)));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", byte.class);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutNull() {
        converter.convert(null, byte.class);
    }

}

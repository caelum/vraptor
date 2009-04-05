package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

public class PrimitiveShortConverterTest {
    
    private PrimitiveShortConverter converter;

    @Before
    public void setup() {
        this.converter = new PrimitiveShortConverter();
    }
    
    @Test
    public void shouldBeAbleToConvertNumbers(){
        assertThat((Short) converter.convert("5"), is(equalTo((short) 5)));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutNull() {
        converter.convert(null);
    }

}

package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

public class PrimitiveLongConverterTest {
    
    private PrimitiveLongConverter converter;

    @Before
    public void setup() {
        this.converter = new PrimitiveLongConverter();
    }
    
    @Test
    public void shouldBeAbleToConvertNumbers(){
        assertThat((Long) converter.convert("2"), is(equalTo(2L)));
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

package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

public class PrimitiveDoubleConverterTest {
    
    private PrimitiveDoubleConverter converter;

    @Before
    public void setup() {
        this.converter = new PrimitiveDoubleConverter();
    }
    
    @Test
    public void shouldBeAbleToConvertNumbers(){
        assertThat((Double) converter.convert("2.3", double.class), is(equalTo(2.3d)));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", double.class);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutNull() {
        converter.convert(null, double.class);
    }

}

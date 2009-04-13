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
    
    @Test
    public void shouldConvertToZeroWhenNull() {
    	assertThat((Double) converter.convert(null, double.class), is(equalTo(0D)));
    }

    @Test
    public void shouldConvertToZeroWhenEmpty() {
    	assertThat((Double) converter.convert("", double.class), is(equalTo(0D)));
    }

}

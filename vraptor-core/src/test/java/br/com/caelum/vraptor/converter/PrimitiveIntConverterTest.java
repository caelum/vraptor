package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

public class PrimitiveIntConverterTest {
    
    private PrimitiveIntConverter converter;

    @Before
    public void setup() {
        this.converter = new PrimitiveIntConverter();
    }
    
    @Test
    public void shouldBeAbleToConvertNumbers(){
        assertThat((Integer) converter.convert("2", int.class, errors, bundle), is(equalTo(2)));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", int.class, errors, bundle);
    }
    
    @Test
    public void shouldConvertToZeroWhenNull() {
        assertThat((Integer) converter.convert(null, int.class, errors, bundle), is(equalTo(0)));
    }

    @Test
    public void shouldConvertToZeroWhenEmpty() {
        assertThat((Integer) converter.convert("", int.class, errors, bundle), is(equalTo(0)));
    }

}

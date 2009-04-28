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
        assertThat((Short) converter.convert("5", short.class, errors, bundle), is(equalTo((short) 5)));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", short.class, errors, bundle);
    }
    
    @Test
    public void shouldConvertToZeroWhenNull() {
    	assertThat((Short) converter.convert(null, short.class, errors, bundle), is(equalTo((short) 0)));
    }

    @Test
    public void shouldConvertToZeroWhenEmpty() {
    	assertThat((Short) converter.convert("", short.class, errors, bundle), is(equalTo((short) 0)));
    }

}

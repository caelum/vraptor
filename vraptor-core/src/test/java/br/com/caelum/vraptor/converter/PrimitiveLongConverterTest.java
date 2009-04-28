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
        assertThat((Long) converter.convert("2", long.class, errors, bundle), is(equalTo(2L)));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", long.class, errors, bundle);
    }
    
    @Test
    public void shouldConvertToZeroWhenNull() {
    	assertThat((Long) converter.convert(null, long.class, errors, bundle), is(equalTo(0L)));
    }

    @Test
    public void shouldConvertToZeroWhenEmpty() {
    	assertThat((Long) converter.convert("", long.class, errors, bundle), is(equalTo(0L)));
    }
    
}

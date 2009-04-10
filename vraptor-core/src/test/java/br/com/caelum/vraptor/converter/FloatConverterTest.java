package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;

public class FloatConverterTest {
    
    private FloatConverter converter;

    @Before
    public void setup() {
        this.converter = new FloatConverter();
    }
    
    @Test
    public void shouldBeAbleToConvertNumbers(){
        assertThat((Float) converter.convert("2.3", Float.class), is(equalTo(2.3f)));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", Float.class);
    }
    
    @Test
    public void shouldComplainAboutNull() {
        assertThat(converter.convert(null, Float.class), is(nullValue()));
    }

}

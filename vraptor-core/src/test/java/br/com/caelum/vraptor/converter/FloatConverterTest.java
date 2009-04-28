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
        assertThat((Float) converter.convert("2.3", Float.class, errors, bundle), is(equalTo(2.3f)));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", Float.class, errors, bundle);
    }
    
    @Test
    public void shouldNotComplainAboutNull() {
        assertThat(converter.convert(null, Float.class, errors, bundle), is(nullValue()));
    }

    @Test
    public void shouldNotComplainAboutEmpty() {
        assertThat(converter.convert("", Float.class, errors, bundle), is(nullValue()));
    }

}

package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;

public class ByteConverterTest {
    
    private ByteConverter converter;

    @Before
    public void setup() {
        this.converter = new ByteConverter();
    }
    
    @Test
    public void shouldBeAbleToConvertNumbers(){
        assertThat((Byte) converter.convert("2", Byte.class, errors, bundle), is(equalTo((byte) 2)));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", Byte.class, errors, bundle);
    }
    
    @Test
    public void shouldNotComplainAboutNull() {
        assertThat(converter.convert(null, Byte.class, errors, bundle), is(nullValue()));
    }

    @Test
    public void shouldNotComplainAboutEmpty() {
        assertThat(converter.convert("", Byte.class, errors, bundle), is(nullValue()));
    }

}

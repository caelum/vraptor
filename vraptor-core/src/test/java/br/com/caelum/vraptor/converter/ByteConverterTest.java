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
        assertThat((Byte) converter.convert("2", Byte.class), is(equalTo((byte) 2)));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", Byte.class);
    }
    
    @Test
    public void shouldComplainAboutNull() {
        assertThat(converter.convert(null, Byte.class), is(nullValue()));
    }

}

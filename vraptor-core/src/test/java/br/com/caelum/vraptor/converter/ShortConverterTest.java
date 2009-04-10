package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;

public class ShortConverterTest {
    
    private ShortConverter converter;

    @Before
    public void setup() {
        this.converter = new ShortConverter();
    }
    
    @Test
    public void shouldBeAbleToConvertNumbers(){
        assertThat((Short) converter.convert("2", Short.class), is(equalTo((short)2)));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", Short.class);
    }
    
    @Test
    public void shouldComplainAboutNull() {
        assertThat(converter.convert(null, Short.class), is(nullValue()));
    }

}

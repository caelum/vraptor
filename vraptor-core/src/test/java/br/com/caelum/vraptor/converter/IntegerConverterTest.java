package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;

public class IntegerConverterTest {
    
    private IntegerConverter converter;

    @Before
    public void setup() {
        this.converter = new IntegerConverter();
    }
    
    @Test
    public void shouldBeAbleToConvertNumbers(){
        assertThat((Integer) converter.convert("2", Integer.class), is(equalTo(2)));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", Integer.class);
    }
    
    @Test
    public void shouldNotComplainAboutNull() {
        assertThat(converter.convert(null, Integer.class), is(nullValue()));
    }

}

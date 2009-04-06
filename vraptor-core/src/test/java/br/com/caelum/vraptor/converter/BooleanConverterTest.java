package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;

public class BooleanConverterTest {
    
    private BooleanConverter converter;

    @Before
    public void setup() {
        this.converter = new BooleanConverter();
    }
    
    @Test
    public void shouldBeAbleToConvertTrueAndFalse(){
        assertThat((Boolean) converter.convert("true", Boolean.class), is(equalTo(true)));
        assertThat((Boolean) converter.convert("True", Boolean.class), is(equalTo(true)));
        assertThat((Boolean) converter.convert("shhshs", Boolean.class), is(equalTo(false)));
        assertThat((Boolean) converter.convert("false", Boolean.class), is(equalTo(false)));
    }
    
    @Test
    public void shouldNotComplainAboutNull() {
        assertThat(converter.convert(null, Boolean.class), is(nullValue()));
    }

}

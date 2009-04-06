package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;

public class LongConverterTest {
    
    private LongConverter converter;

    @Before
    public void setup() {
        this.converter = new LongConverter();
    }
    
    @Test
    public void shouldBeAbleToConvertNumbers(){
        assertThat((Long) converter.convert("2", long.class), is(equalTo(2L)));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", long.class);
    }
    
    @Test
    public void shouldNotComplainAboutNull() {
        assertThat(converter.convert(null, long.class), is(nullValue()));
    }

}

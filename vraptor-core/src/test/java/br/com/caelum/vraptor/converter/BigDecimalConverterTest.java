package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

/**
 * VRaptor's BigDecimal converter test. 
 * 
 * @author Cecilia Fernandes
 */
public class BigDecimalConverterTest {
    
    private BigDecimalConverter converter;

    @Before
    public void setup() {
        this.converter = new BigDecimalConverter();
    }
    
    @Test
    public void shouldBeAbleToConvertIntegerNumbers(){
        assertThat((BigDecimal) converter.convert("2.3", BigDecimal.class), is(equalTo(new BigDecimal("2.3"))));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", BigDecimal.class);
    }
    
    @Test
    public void shouldNotComplainAboutNull() {
        assertThat(converter.convert(null, BigDecimal.class), is(nullValue()));
    }

    @Test
    public void shouldNotComplainAboutEmpty() {
        assertThat(converter.convert("", BigDecimal.class), is(nullValue()));
    }

}

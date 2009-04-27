package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

/**
 * VRaptor's BigInteger converter test. 
 * 
 * @author Cecilia Fernandes
 */
public class BigIntegerConverterTest {
    
    private BigIntegerConverter converter;

    @Before
    public void setup() {
        this.converter = new BigIntegerConverter();
    }
    
    @Test
    public void shouldBeAbleToConvertIntegerNumbers(){
        assertThat((BigInteger) converter.convert("3", BigInteger.class), is(equalTo(new BigInteger("3"))));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutNonIntegerNumbers() {
        converter.convert("2.3", BigInteger.class);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", BigInteger.class);
    }
    
    @Test
    public void shouldNotComplainAboutNull() {
        assertThat(converter.convert(null, BigInteger.class), is(nullValue()));
    }

    @Test
    public void shouldNotComplainAboutEmpty() {
        assertThat(converter.convert("", BigInteger.class), is(nullValue()));
    }

}

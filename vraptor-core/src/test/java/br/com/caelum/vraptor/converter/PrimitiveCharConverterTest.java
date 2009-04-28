package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

public class PrimitiveCharConverterTest {
    
    private PrimitiveCharConverter converter;

    @Before
    public void setup() {
        this.converter = new PrimitiveCharConverter();
    }
    
    @Test
    public void shouldBeAbleToConvertNumbers(){
        assertThat(((Character) converter.convert("r", char.class, errors, bundle)).charValue(), is(equalTo('r')));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", char.class, errors, bundle);
    }
    
    @Test
    public void shouldConvertToZeroWhenNull() {
        assertThat(((Character) converter.convert(null, char.class, errors, bundle)).charValue(), is(equalTo('\u0000')));
    }

    @Test
    public void shouldConvertToZeroWhenEmpty() {
        assertThat(((Character) converter.convert("", char.class, errors, bundle)).charValue(), is(equalTo('\u0000')));
    }

}

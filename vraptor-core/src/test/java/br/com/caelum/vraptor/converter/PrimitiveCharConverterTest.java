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
        assertThat(((Character) converter.convert("r", char.class)).charValue(), is(equalTo('r')));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", char.class);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutNull() {
        converter.convert(null, char.class);
    }

}

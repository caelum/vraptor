package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Before;
import org.junit.Test;

public class CharacterConverterTest {
    
    private CharacterConverter converter;

    @Before
    public void setup() {
        this.converter = new CharacterConverter();
    }
    
    @Test
    public void shouldBeAbleToConvertCharacters(){
        assertThat((Character) converter.convert("Z", Character.class, errors, bundle), is(equalTo(new Character('Z'))));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldComplainAboutStringTooBig() {
        converter.convert("---", Character.class, errors, bundle);
    }
    
    @Test
    public void shouldNotComplainAboutNullAndEmpty() {
        assertThat(converter.convert(null, Character.class, errors, bundle), is(nullValue()));
        assertThat(converter.convert("", Character.class, errors, bundle), is(nullValue()));
    }


}

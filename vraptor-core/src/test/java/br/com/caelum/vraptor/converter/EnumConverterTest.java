package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;

public class EnumConverterTest {

    private EnumConverter converter;

    @Before
    public void setup() {
        this.converter = new EnumConverter();
    }

    @Test
    public void shouldBeAbleToConvertByOrdinal() {
        assertThat((MyCustomEnum) converter.convert("1", MyCustomEnum.class, errors, bundle), is(equalTo(MyCustomEnum.SECOND)));
    }

    @Test
    public void shouldBeAbleToConvertByName() {
        assertThat((MyCustomEnum) converter.convert("FIRST", MyCustomEnum.class, errors, bundle), is(equalTo(MyCustomEnum.FIRST)));
    }

    @Test
    public void shouldConvertEmptyToNull() {
        assertThat(converter.convert("", MyCustomEnum.class, errors, bundle), is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldComplainAboutInvalidIndex() {
        converter.convert("3200", MyCustomEnum.class, errors, bundle);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("32a00", MyCustomEnum.class, errors, bundle);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldComplainAboutInvalidOrdinal() {
        converter.convert("THIRD", MyCustomEnum.class, errors, bundle);
    }

    @Test
    public void shouldAcceptNull() {
        assertThat((MyCustomEnum) converter.convert(null, MyCustomEnum.class, errors, bundle), is(nullValue()));
    }

    enum MyCustomEnum {
        FIRST, SECOND
    }

}

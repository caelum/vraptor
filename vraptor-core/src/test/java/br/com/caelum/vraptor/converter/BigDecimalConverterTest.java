package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.validator.ValidationMessage;

/**
 * VRaptor's BigDecimal converter test. 
 * 
 * @author Cecilia Fernandes
 */
public class BigDecimalConverterTest {
    
    private BigDecimalConverter converter;
	private List<ValidationMessage> errors;
	private ResourceBundle bundle;

    @Before
    public void setup() {
        this.converter = new BigDecimalConverter();
        this.errors = new ArrayList<ValidationMessage>();
        this.bundle = ResourceBundle.getBundle("messages");
    }
    
    @Test
    public void shouldBeAbleToConvertIntegerNumbers(){
        assertThat(converter.convert("2.3", BigDecimal.class, errors, bundle), is(equalTo(new BigDecimal("2.3"))));
    }
    
    @Test
    public void shouldComplainAboutInvalidNumber() {
        converter.convert("---", BigDecimal.class, errors, bundle);
        assertThat(errors.get(0), is(VRaptorMatchers.error("", "--- is not a valid number.")));
    }
    
    @Test
    public void shouldNotComplainAboutNull() {
        assertThat(converter.convert(null, BigDecimal.class, errors, bundle), is(nullValue()));
    }

    @Test
    public void shouldNotComplainAboutEmpty() {
        assertThat(converter.convert("", BigDecimal.class, errors, bundle), is(nullValue()));
    }

}

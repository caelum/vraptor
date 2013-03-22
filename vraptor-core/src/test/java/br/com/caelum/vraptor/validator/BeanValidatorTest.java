package br.com.caelum.vraptor.validator;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Locale;

import javax.validation.groups.Default;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.core.Localization;

/**
 * A simple class to test JSR303Validator and HibernateValidator3 components.
 *
 * @author Otávio Scherer Garcia
 * @since 3.1.2
 */
public class BeanValidatorTest {

	private @Mock Localization localization;

    private DefaultBeanValidator beanValidator;

    @Before
    public void setup() {
    	Locale.setDefault(new Locale("en"));
    	MockitoAnnotations.initMocks(this);

    	ValidatorFactoryCreator creator = new ValidatorFactoryCreator();
    	creator.buildFactory();

    	ValidatorCreator validatorFactory = new ValidatorCreator(creator.getInstance());
    	validatorFactory.createValidator();

    	MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory(creator.getInstance());
    	interpolatorFactory.createInterpolator();

        beanValidator = new DefaultBeanValidator(localization, validatorFactory.getInstance(), interpolatorFactory.getInstance());
    }

    @Test
    public void withoutViolations() {
        CustomerForValidation customer0 = new CustomerForValidation(10, "Vraptor", 18);
		assertThat(beanValidator.validate(customer0), hasSize(0));
    }

    @Test
    public void shouldValidate() {
        CustomerForValidation customer0 = new CustomerForValidation(null, null, null);
		assertThat(beanValidator.validate(customer0), not(hasSize(0)));
    }
    
    @Test
    public void shouldValidateWithMyLocale() {
    	when(localization.getLocale()).thenReturn(new Locale("pt", "br"));
        CustomerForValidation customer0 = new CustomerForValidation(null, null, null);
		List<Message> messages = beanValidator.validate(customer0);
		
		assertThat(messages, not(hasSize(0)));
		assertThat(messages.toString(), containsString("não pode ser nulo"));
    }
    
    @Test
    public void shouldValidateWithDefaultLocale() {
    	when(localization.getLocale()).thenReturn(null);
        CustomerForValidation customer0 = new CustomerForValidation(null, null, null);
		List<Message> messages = beanValidator.validate(customer0);
		
		assertThat(messages, not(hasSize(0)));
		assertThat(messages.toString(), containsString("may not be null"));
    }
    
    @Test
    public void shouldReturnEmptyCollectionIsBeanIsNull() {
    	assertThat(beanValidator.validate(null), hasSize(0));
    }
    
    @Test
    public void nullValidatorShouldNeverValidate() {
    	BeanValidator validator = new NullBeanValidator();
    	assertThat(validator.validate(null), hasSize(0));
    	assertThat(validator.validate(new Object()), hasSize(0));
    	assertThat(validator.validate(new CustomerForValidation(null, null, null)), hasSize(0));
    }
    
    @Test
    public void shouldValidateOneSpecifiedGroup() {
    	CustomerForValidation customer0 = new CustomerForValidation(null, null, null);
    	assertThat(beanValidator.validate(customer0, SomeGroupValidation.class), hasSize(1));
    }
    
    @Test
    public void shouldValidateManySpecifiedGroups() {
    	CustomerForValidation customer0 = new CustomerForValidation(null, null, null);
    	assertThat(beanValidator.validate(customer0, SomeGroupValidation.class, AnotherGroupValidation.class), hasSize(2));
    }
    
    @Test
    public void shouldValidateOnlySpecifiedProperties() {
    	CustomerForValidation customer0 = new CustomerForValidation(null, null, null);
    	assertThat(beanValidator.validateProperties(customer0, "id"), hasSize(1));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoPropertiesWereSpecified() {
    	CustomerForValidation customer0 = new CustomerForValidation(null, null, null);
    	beanValidator.validateProperties(customer0);
    }
    
    @Test
    public void shouldReturnEmptyCollectionIfBeanIsNullForValidationProperties() {
    	assertThat(beanValidator.validateProperties(null, "id"), hasSize(0));
    }

    /**
     * Customer for using in bean validator tests.
     */
    public class CustomerForValidation {

        @javax.validation.constraints.NotNull
        public Integer id;

        @javax.validation.constraints.NotNull(groups={SomeGroupValidation.class, Default.class})
        public String name;

        @javax.validation.constraints.NotNull(groups=AnotherGroupValidation.class)
        public Integer age;
        
        public CustomerForValidation(Integer id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
        
    }
    
    public interface SomeGroupValidation {}
    
    public interface AnotherGroupValidation {}
}

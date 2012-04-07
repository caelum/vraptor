package br.com.caelum.vraptor.validator;

import org.junit.Assert;
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
    	MockitoAnnotations.initMocks(this);

    	ValidatorFactoryCreator creator = new ValidatorFactoryCreator();
    	creator.buildFactory();

    	ValidatorCreator validatorFactory = new ValidatorCreator(creator.getInstance());
    	validatorFactory.createValidator();

    	MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory(creator.getInstance());
    	interpolatorFactory.createInterpolator();

        this.beanValidator = new DefaultBeanValidator(localization, validatorFactory.getInstance(), interpolatorFactory.getInstance());
    }

    @Test
    public void withoutViolations() {
        CustomerForValidation customer0 = new CustomerForValidation(10, "Vraptor");
        Assert.assertTrue(beanValidator.validate(customer0).isEmpty());
    }

    @Test
    public void withViolations() {
        CustomerForValidation customer0 = new CustomerForValidation(null, null);
        Assert.assertFalse(beanValidator.validate(customer0).isEmpty());
    }

    /**
     * Customer for using in bean validator tests.
     */
    public class CustomerForValidation {

        @javax.validation.constraints.NotNull
        public Integer id;

        @javax.validation.constraints.NotNull
        public String name;

        public CustomerForValidation(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}

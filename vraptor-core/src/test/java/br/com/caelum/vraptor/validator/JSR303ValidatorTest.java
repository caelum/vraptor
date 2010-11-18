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
public class JSR303ValidatorTest {

	private @Mock Localization localization;

    private JSR303Validator jsr303Validator;

    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);

    	ValidatorFactoryCreator creator = new ValidatorFactoryCreator();
    	creator.buildFactory();

    	JSR303ValidatorFactory validatorFactory = new JSR303ValidatorFactory(creator.getInstance());
    	validatorFactory.createValidator();

    	MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory(creator.getInstance());
    	interpolatorFactory.createInterpolator();

        this.jsr303Validator = new JSR303Validator(localization, validatorFactory.getInstance(), interpolatorFactory.getInstance());
    }

    @Test
    public void withoutViolations() {
        CustomerJSR303 customer0 = new CustomerJSR303(10, "Vraptor");
        Assert.assertTrue(jsr303Validator.validate(customer0).isEmpty());
    }

    @Test
    public void withViolations() {
        CustomerJSR303 customer0 = new CustomerJSR303(null, null);
        Assert.assertFalse(jsr303Validator.validate(customer0).isEmpty());
    }

    /**
     * Customer for using in bean validator tests.
     */
    public class CustomerJSR303 {

        @javax.validation.constraints.NotNull
        public Integer id;

        @javax.validation.constraints.NotNull
        public String name;

        public CustomerJSR303(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}

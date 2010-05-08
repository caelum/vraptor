package br.com.caelum.vraptor.validator;

import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.test.VRaptorMockery;

/**
 * A simple class to test JSR303Validator and HibernateValidator3 components.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.1.2
 */
public class JSR303ValidatorTest {

    private VRaptorMockery mockery;

    private JSR303Validator jsr303Validator;
    private HibernateValidator3 hibernateValidator3;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();

        final Localization localization = mockery.localization();

        this.jsr303Validator = new JSR303Validator(localization);
        this.hibernateValidator3 = new HibernateValidator3(localization);

        mockery.checking(new Expectations() {
            {
                allowing(localization).getLocale();
            }
        });
    }

    @Test
    public void withoutViolations() {
        CustomerJSR303 customer0 = new CustomerJSR303(10, "Vraptor");
        Assert.assertTrue(jsr303Validator.validate(customer0).isEmpty());

        CustomerHibernate3 customer1 = new CustomerHibernate3(10, "Vraptor");
        Assert.assertTrue(hibernateValidator3.validate(customer1).isEmpty());
    }

    @Test
    public void withViolations() {
        CustomerJSR303 customer0 = new CustomerJSR303(null, null);
        Assert.assertFalse(jsr303Validator.validate(customer0).isEmpty());

        CustomerHibernate3 customer1 = new CustomerHibernate3(null, null);
        Assert.assertFalse(hibernateValidator3.validate(customer1).isEmpty());
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

    /**
     * Customer for using in hibernate validator tests.
     */
    public class CustomerHibernate3 {

        @org.hibernate.validator.NotNull
        public Integer id;

        @org.hibernate.validator.NotNull
        public String name;

        public CustomerHibernate3(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}

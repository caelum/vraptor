package br.com.caelum.vraptor.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.core.Localization;

public class HibernateValidator3Test {

	private @Mock Localization localization;
	private HibernateValidator3 hibernateValidator3;


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.hibernateValidator3 = new HibernateValidator3(localization);
	}

	@Test
	public void withoutViolations() throws Exception {
        CustomerHibernate3 customer1 = new CustomerHibernate3(10, "Vraptor");
        Assert.assertTrue(hibernateValidator3.validate(customer1).isEmpty());
	}

	@Test
	public void withViolations() throws Exception {
        CustomerHibernate3 customer1 = new CustomerHibernate3(null, null);
        Assert.assertFalse(hibernateValidator3.validate(customer1).isEmpty());
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

package br.com.caelum.vraptor.util.test;

import br.com.caelum.vraptor.validator.HibernateValidator3;

/**
 * Mocked Validator that uses Hibernate validator for validate method
 *
 * @author Wagner Ferreira and leandros
 * @see MockValidator
 * @deprecated Use {@link JSR303MockValidator} instead.
 */
@Deprecated
public class HibernateMockValidator extends MockValidator {

	private HibernateValidator3 hibernateValidator3;

	public HibernateMockValidator() {
		hibernateValidator3 = new HibernateValidator3(new MockLocalization());
	}

	@Override
	public void validate(Object bean) {
		addAll(hibernateValidator3.validate(bean));
	}

}

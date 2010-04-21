package br.com.caelum.vraptor.validator;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;

/**
 * Implements the {@link BeanValidator} using the Hibernate Validator 3.x. This implementation will be enable by vraptor
 * when the hibernate validator classes is locale in classpath.
 * 
 * @author Guilherme Silveira
 * @since 3.1.2
 */
public class HibernateValidator3
    implements BeanValidator {

    private static final ValidatorLocator locator = new ValidatorLocator();

    @SuppressWarnings("unchecked")
    @Override
    public List<Message> validate(Object object) {
        List<Message> errors = new ArrayList<Message>();
        ClassValidator<Object> validator = (ClassValidator<Object>) locator.getValidator(object.getClass(), null);
        InvalidValue[] invalidValues = validator.getInvalidValues(object);

        for (InvalidValue value : invalidValues) {
            errors.add(new ValidationMessage(value.getMessage(), value.getPropertyPath()));
        }
        return errors;
    }
}

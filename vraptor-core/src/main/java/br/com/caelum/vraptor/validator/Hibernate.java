
package br.com.caelum.vraptor.validator;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;

/**
 * Uses hibernate's validator api to check for error messages.
 *
 * @author Guilherme Silveira
 */
public class Hibernate {

    private static final ValidatorLocator locator = new ValidatorLocator();

    @SuppressWarnings("unchecked")
    public static List<Message> validate(Object instance) {
        List<Message> errors = new ArrayList<Message>();
        ClassValidator<Object> validator = (ClassValidator<Object>) locator.getValidator(instance.getClass(), null);
        InvalidValue[] invalidValues = validator.getInvalidValues(instance);

        for (InvalidValue value : invalidValues) {
            errors.add(new ValidationMessage(value.getMessage(), value.getPropertyPath()));
        }
        return errors;
    }

}

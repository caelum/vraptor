package br.com.caelum.vraptor.validator;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;

public class Hibernate {
    
    private static final ValidatorLocator locator= new ValidatorLocator();

    @SuppressWarnings("unchecked")
    public static List<String> validate(Object instance) {
        List<String> errors = new ArrayList<String>();
        ClassValidator<Object> validator = (ClassValidator<Object>) locator.getValidator(instance.getClass(), null);
        InvalidValue[] invalidValues = validator.getInvalidValues(instance);

        for (InvalidValue value : invalidValues) {
            errors.add(value.getMessage());
        }
        return errors;
    }

}

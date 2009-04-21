package br.com.caelum.vraptor.validator;

import java.util.List;

/**
 * The default validator implementation.
 * 
 * @author Guilherme Silveira
 */
public class DefaultValidator {

    public void checking(Validations validations) {
        List<String> errors = validations.getErrors();
        if(!errors.isEmpty()) {
            throw new ValidationError(errors);
        }
    }

}

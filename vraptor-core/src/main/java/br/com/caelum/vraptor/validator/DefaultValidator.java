package br.com.caelum.vraptor.validator;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.view.jsp.PageResult;

import java.util.List;

/**
 * The default validator implementation.
 *
 * @author Guilherme Silveira
 */
public class DefaultValidator implements Validator {

    private final PageResult result;

    public DefaultValidator(PageResult result) {
        this.result = result;
    }

    public void checking(Validations validations) {
        List<String> errors = validations.getErrors();
        if (!errors.isEmpty()) {
            result.include("errors", errors);
            result.forward("invalid");
            // finished just fine
            throw new ValidationError(errors);
        }
    }

}

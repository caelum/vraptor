package br.com.caelum.vraptor.validator;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import br.com.caelum.vraptor.view.jsp.PageResult;

/**
 * The default validator implementation.
 * 
 * @author Guilherme Silveira
 */
public class DefaultValidator {
    
    private final PageResult result;

    public DefaultValidator(PageResult result) {
        this.result = result;
    }

    public void checking(Validations validations) {
        List<String> errors = validations.getErrors();
        if(!errors.isEmpty()) {
            try {
                result.forward("invalid");
                // finished just fine
                throw new ValidationError(errors);
            } catch (ServletException e) {
                throw new InvalidValidationProcess(e);
            } catch (IOException e) {
                throw new InvalidValidationProcess(e);
            }
        }
    }

}

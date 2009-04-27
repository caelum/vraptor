package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationError;
import br.com.caelum.vraptor.validator.Validations;
import br.com.caelum.vraptor.view.jsp.PageResult;
import org.vraptor.i18n.FixedMessage;
import org.vraptor.validator.ValidationErrors;

import java.util.List;

/**
 * The vraptor2 compatible messages creator.
 *
 * @author Guilherme Silveira
 */
public class MessageCreatorValidator implements Validator {

    private final PageResult result;
    private final ValidationErrors errors;

    public MessageCreatorValidator(PageResult result, ValidationErrors errors) {
        this.result = result;
        this.errors = errors;
    }

    public void checking(Validations validations) {
        List<String> messages = validations.getErrors();
        if (!messages.isEmpty()) {
            for (String s : messages) {
                this.errors.add(new FixedMessage("", s, ""));
            }
            result.include("errors", this.errors);
            result.forward("invalid");
            // finished just fine
            throw new ValidationError(messages);
        }
    }

}

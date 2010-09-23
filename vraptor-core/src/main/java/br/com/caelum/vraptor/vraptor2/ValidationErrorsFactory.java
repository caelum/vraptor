package br.com.caelum.vraptor.vraptor2;

import org.vraptor.validator.BasicValidationErrors;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;

@Component
public class ValidationErrorsFactory implements ComponentFactory<ValidationErrors>{

	private BasicValidationErrors validationErrors = new BasicValidationErrors();

	public ValidationErrors getInstance() {
		return validationErrors;
	}

}

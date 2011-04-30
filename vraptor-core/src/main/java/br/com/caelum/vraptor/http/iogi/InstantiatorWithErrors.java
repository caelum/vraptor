package br.com.caelum.vraptor.http.iogi;

import java.util.List;

import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.vraptor.validator.Message;

public interface InstantiatorWithErrors {

	Object instantiate(Target<?> target, Parameters parameters, List<Message> errors);

}

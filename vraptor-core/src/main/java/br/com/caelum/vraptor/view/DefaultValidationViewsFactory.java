package br.com.caelum.vraptor.view;

import java.util.List;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationError;

/**
 * Default implementation for ValidationViewsFactory
 * @author Lucas Cavalcanti
 * @author Pedro Matiello
 */
public class DefaultValidationViewsFactory implements ValidationViewsFactory {

	private final Result result;
	private final Proxifier proxifier;

	public DefaultValidationViewsFactory(Result result, Proxifier proxifier) {
		this.result = result;
		this.proxifier = proxifier;
	}

	public <T extends View> T instanceFor(Class<T> view, List<Message> errors) {
		T viewInstance = result.use(view);
		if (view.equals(LogicResult.class)) {
			return view.cast(new ValidationLogicResult((LogicResult) viewInstance, proxifier, errors));
		}
		if (view.equals(PageResult.class)) {
			return view.cast(new ValidationPageResult((PageResult) viewInstance, proxifier, errors));
		}
		if (view.equals(EmptyResult.class)) {
			throw new ValidationError(errors);
		}
		throw new ResultException("There is no validation version of " + view);
	}
}

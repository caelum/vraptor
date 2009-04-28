package br.com.caelum.vraptor.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodParameters;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.validator.Validations;

/**
 * An interceptor which instantiates parameters and provide them to the stack.
 * 
 * @author Guilherme Silveira
 */
public class ParametersInstantiatorInterceptor implements Interceptor {

	private final ParametersProvider provider;
	private final MethodParameters parameters;
	private final ParameterNameProvider nameProvider;

	private static final Logger logger = LoggerFactory.getLogger(ParametersInstantiatorInterceptor.class);
	private final Validator validator;

	public ParametersInstantiatorInterceptor(ParametersProvider provider, MethodParameters parameters,
			ParameterNameProvider nameProvider, Validator validator) {
		this.provider = provider;
		this.parameters = parameters;
		this.nameProvider = nameProvider;
		this.validator = validator;
	}

	public boolean accepts(ResourceMethod method) {
		return true;
	}

	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
			throws InterceptionException {
		ResourceBundle bundle = null;
		final List<ValidationMessage> errors = new ArrayList<ValidationMessage>();
		Object[] values = provider.getParametersFor(method, errors, bundle);
		if (!errors.isEmpty()) {
			validator.checking(new Validations() {
				@Override
				public List<ValidationMessage> getErrors() {
					return errors;
				}
			});
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Parameter values for " + method + " are " + Arrays.asList(values));
		}
		parameters.set(values, nameProvider.parameterNamesFor(method.getMethod()));
		stack.next(method, resourceInstance);
	}

}

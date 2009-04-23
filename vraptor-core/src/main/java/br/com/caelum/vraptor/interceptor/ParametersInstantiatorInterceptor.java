package br.com.caelum.vraptor.interceptor;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodParameters;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;

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

    public ParametersInstantiatorInterceptor(ParametersProvider provider, MethodParameters parameters,
            ParameterNameProvider nameProvider) {
        this.provider = provider;
        this.parameters = parameters;
        this.nameProvider = nameProvider;
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
        Object[] values = provider.getParametersFor(method);
        if (logger.isDebugEnabled()) {
            logger.debug("Parameter values for " + method + " are " + Arrays.asList(values));
        }
        parameters.set(values, nameProvider.parameterNamesFor(method.getMethod()));
        stack.next(method, resourceInstance);
    }

}

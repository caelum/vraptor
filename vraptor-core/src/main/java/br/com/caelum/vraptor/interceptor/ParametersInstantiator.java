package br.com.caelum.vraptor.interceptor;

import java.io.IOException;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodParameters;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class ParametersInstantiator implements Interceptor{

    private final ParametersProvider provider;
    private final MethodParameters parameters;
    private final ParameterNameProvider nameProvider;

    public ParametersInstantiator(ParametersProvider provider, MethodParameters parameters, ParameterNameProvider nameProvider) {
        this.provider = provider;
        this.parameters = parameters;
        this.nameProvider = nameProvider;
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws IOException,
            InterceptionException {
        parameters.set(provider.getParametersFor(method), nameProvider.parameterNamesFor(method.getMethod()));
        stack.next(method, resourceInstance);
    }

}

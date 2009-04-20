package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.core.URLParameterExtractorInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.ParametersInstantiator;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;

/**
 * A vraptor 2 request execution process.
 * 
 * @author Guilherme Silveira
 */
public class VRaptor2RequestExecution implements RequestExecution {
    private final InterceptorStack interceptorStack;
    private final InstantiateInterceptor instantiator;
    private boolean shouldRegisterHibernateValidator;

    public VRaptor2RequestExecution(InterceptorStack interceptorStack, InstantiateInterceptor instantiator, Config config) {
        this.interceptorStack = interceptorStack;
        this.instantiator = instantiator;
        this.shouldRegisterHibernateValidator = config.hasPlugin("org.vraptor.plugin.hibernate.HibernateValidatorPlugin");
    }

    public void execute() throws IOException, InterceptionException {
        interceptorStack.add(ResourceLookupInterceptor.class);
        interceptorStack.add(URLParameterExtractorInterceptor.class);
        interceptorStack.add(InterceptorListPriorToExecutionExtractor.class);
        if(shouldRegisterHibernateValidator) {
            interceptorStack.add(HibernateValidatorPluginInterceptor.class);
        }
        interceptorStack.add(instantiator);
        interceptorStack.add(ParametersInstantiator.class);
        interceptorStack.add(ResultSupplierInterceptor.class);
        interceptorStack.add(Validator.class);
        interceptorStack.add(ExecuteAndViewInterceptor.class);
        interceptorStack.add(OutjectionInterceptor.class);
        interceptorStack.add(AjaxInterceptor.class);
        interceptorStack.add(ViewInterceptor.class);
        interceptorStack.next(null, null);
    }
}

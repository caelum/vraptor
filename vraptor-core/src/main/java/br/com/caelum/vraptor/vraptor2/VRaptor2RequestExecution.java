package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;

/**
 * A vraptor 2 request execution process.
 * 
 * @author Guilherme Silveira
 */
public class VRaptor2RequestExecution implements RequestExecution {
    private final InterceptorStack interceptorStack;
    private final InstantiateInterceptor instantiator;

    public VRaptor2RequestExecution(InterceptorStack interceptorStack, InstantiateInterceptor instantiator) {
        this.interceptorStack = interceptorStack;
        this.instantiator = instantiator;
    }

    public void execute() throws IOException, InterceptionException {
        interceptorStack.add(ResourceLookupInterceptor.class);
        interceptorStack.add(instantiator);
        interceptorStack.add(InterceptorListPriorToExecutionExtractor.class);
        interceptorStack.add(ExecuteAndViewInterceptor.class);
        interceptorStack.next(null, null);
    }
}

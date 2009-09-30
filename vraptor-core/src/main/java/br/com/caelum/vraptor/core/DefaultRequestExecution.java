
package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.extra.ForwardToDefaultViewInterceptor;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.OutjectResult;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.interceptor.download.DownloadInterceptor;
import br.com.caelum.vraptor.interceptor.multipart.MultipartInterceptor;
import br.com.caelum.vraptor.ioc.RequestScoped;

/**
 * A request execution process. The default order is extremely important but
 * this behaviour can be change by providing your own RequestExecution.
 *
 * @author Guilherme Silveira
 */
@RequestScoped
public class DefaultRequestExecution implements RequestExecution {
    private final InterceptorStack interceptorStack;
    private final InstantiateInterceptor instantiator;

    public DefaultRequestExecution(InterceptorStack interceptorStack, InstantiateInterceptor instantiator) {
        this.interceptorStack = interceptorStack;
        this.instantiator = instantiator;
    }

    public void execute() throws InterceptionException {
        interceptorStack.add(ResourceLookupInterceptor.class);
        interceptorStack.add(URLParameterExtractorInterceptor.class);
        interceptorStack.add(InterceptorListPriorToExecutionExtractor.class);
        interceptorStack.add(MultipartInterceptor.class);
        interceptorStack.add(instantiator);
        interceptorStack.add(ParametersInstantiatorInterceptor.class);
        interceptorStack.add(ExecuteMethodInterceptor.class);
        interceptorStack.add(OutjectResult.class);
        interceptorStack.add(DownloadInterceptor.class);
        interceptorStack.add(ForwardToDefaultViewInterceptor.class);
        interceptorStack.next(null, null);
    }
}

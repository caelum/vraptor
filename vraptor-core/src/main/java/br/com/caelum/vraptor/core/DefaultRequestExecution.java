package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.resource.ResourceMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A request execution process.
 *
 * @author Guilherme Silveira
 */
public class DefaultRequestExecution implements RequestExecution {
    private UrlToResourceTranslator translator;
    private InterceptorStack interceptorStack;
    private InstantiateInterceptor instantiateInterceptor;

    public DefaultRequestExecution(UrlToResourceTranslator translator, InterceptorStack interceptorStack,
            InstantiateInterceptor instantiateInterceptor) {
        this.translator = translator;
        this.interceptorStack = interceptorStack;
        this.instantiateInterceptor = instantiateInterceptor;
    }

    public void execute(VRaptorRequest request) throws IOException {
        HttpServletResponse response = request.getResponse();
        ResourceMethod method = translator.translate(request.getRequest());
        if (method == null) {
            response.setStatus(404);
            response.getWriter().println("resource not found");
            return;
        }

        PrintWriter out = response.getWriter();
        out.println("executing resource " + method);
        interceptorStack.add(instantiateInterceptor);
        interceptorStack.next();
    }
}

package br.com.caelum.vraptor.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class ResourceLookupInterceptor implements Interceptor {

    private final UrlToResourceTranslator translator;
    private final VRaptorRequest request;
    private final Container container;

    public ResourceLookupInterceptor(UrlToResourceTranslator translator, VRaptorRequest request, Container container) {
        this.translator = translator;
        this.request = request;
        // TODO unfortunately we need the container to register the ResourceMethod
        this.container = container;
    }

    public void intercept(InterceptorStack invocation, ResourceMethod ignorableMethod, Object resourceInstance) throws IOException, InterceptionException {
        HttpServletResponse response = request.getResponse();
        ResourceMethod method = translator.translate(request.getRequest());
        if (method == null) {
            response.setStatus(404);
            response.getWriter().println("resource not found");
            return;
        }
        container.register(method);
        invocation.next(method, resourceInstance);
    }

}

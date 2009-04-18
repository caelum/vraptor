package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.vraptor2.outject.DefaultExporter;
import br.com.caelum.vraptor.vraptor2.outject.JsonExporter;

public class ResultSupplierInterceptor implements Interceptor {

    private final Container container;
    private final HttpServletRequest request;
    private final ComponentInfoProvider info;

    public ResultSupplierInterceptor(Container container, HttpServletRequest request, ComponentInfoProvider info) {
        this.container = container;
        this.request = request;
        this.info = info;
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws IOException,
            InterceptionException {
        String view = request.getParameter("view");
        // simple version to do ajax parsing
        if (info.isAjax()) {
            container.register(JsonExporter.class);
        } else {
            container.register(DefaultExporter.class);
        }
        stack.next(method, resourceInstance);
    }

}

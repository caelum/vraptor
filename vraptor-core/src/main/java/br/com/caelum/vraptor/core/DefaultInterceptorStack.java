package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultInterceptorStack implements InterceptorStack {

    private final List<InterceptorHandler> interceptors = new ArrayList<InterceptorHandler>();
    private final Container container;

    public DefaultInterceptorStack(Container container) {
        // TODO here the component factory would be enough
        this.container = container;
    }

    /**
     * we do not use an iterator so an interceptor can hack the code to add new
     * interceptors on the fly
     */
    private int nextInterceptor = 0;

    public void add(Interceptor interceptor) {
        this.interceptors.add(new InstantiatedInterceptorHandler(interceptor));
    }

    public void next(ResourceMethod method, Object resourceInstance) throws IOException, InterceptionException {
        if (nextInterceptor == interceptors.size()) {
            return;
        }
        InterceptorHandler handler = interceptors.get(nextInterceptor++);
        handler.execute(this, method, resourceInstance);
    }

    public <T extends Interceptor> void add(Class<T> type) {
        this.interceptors.add(new ToInstantiateInterceptorHandler(container, type));
    }

}

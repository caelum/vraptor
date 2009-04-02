package br.com.caelum.vraptor.core;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Instantiates the interceptor on the fly and executes its method.
 * 
 * @author Guilherme Silveira
 */
public class ToInstantiateInterceptorHandler implements InterceptorHandler {

    private final Container container;
    private final Class<?> type;
    
    private static final Logger logger = LoggerFactory.getLogger(ToInstantiateInterceptorHandler.class);

    public ToInstantiateInterceptorHandler(Container container, Class<?> type) {
        this.container = container;
        this.type = type;
        // TODO not working, why????
        // if (Interceptor.class.isAssignableFrom(type)) {
        // throw new IllegalArgumentException("Type " + type.getName()
        // + " does not implement the interface " + Interceptor.class.getName()
        // + " interface");
        // }
    }

    public void execute(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws IOException, InterceptionException {
        Interceptor interceptor = Interceptor.class.cast(container.instanceFor(type));
        if(interceptor==null) {
            throw new InterceptionException("Unable to instantiate interceptor for " + type.getName());
        }
        interceptor.intercept(stack, method, resourceInstance);
    }

}


package br.com.caelum.vraptor.interceptor;

import java.util.List;

import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

public interface InterceptorRegistry {

    /**
     * Creates a list of interceptors which should intercept the required method
     * for the specified container (request).
     */
    Interceptor[] interceptorsFor(ResourceMethod method, Container container);

    void register(Class<? extends Interceptor> ... interceptors);

    List<Class<? extends Interceptor>> all();

}

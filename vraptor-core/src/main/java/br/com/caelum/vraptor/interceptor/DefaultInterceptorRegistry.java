
package br.com.caelum.vraptor.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.util.collections.Filters;
import br.com.caelum.vraptor.util.collections.Functions;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * A registry filled with interceptors to intercept requests.<br/>
 * Interceptors are queried wether they want to intercept a request through
 * their accepts method.
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
@ApplicationScoped
public class DefaultInterceptorRegistry implements InterceptorRegistry {

    private final List<Class<? extends Interceptor>> interceptors = new ArrayList<Class<? extends Interceptor>>();

    public Interceptor[] interceptorsFor(ResourceMethod method, Container container) {
		List<Interceptor> list = Lists.transform(interceptors, Functions.<Interceptor>instanceWith(container));
		Collection<Interceptor> filtered = Collections2.filter(list, Filters.accepts(method));
		return filtered.toArray(new Interceptor[0]);
    }

    public void register(Class<? extends Interceptor>... interceptors) {
        this.interceptors.addAll(Arrays.asList(interceptors));
    }

    public List<Class<? extends Interceptor>> all() {
        return interceptors;
    }

}

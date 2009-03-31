package br.com.caelum.vraptor.interceptor;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultInterceptorRegistry implements InterceptorRegistry{

    public Class<? extends Interceptor>[] interceptorsFor(ResourceMethod method) {
        List<Class<? extends Interceptor>> list = new ArrayList<Class<? extends Interceptor>>();
        return list.toArray(new Class[list.size()]);
    }

}

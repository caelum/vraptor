package br.com.caelum.vraptor.interceptor;

import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

public interface InterceptorRegistry {

    Class<? extends Interceptor>[] interceptorsFor(ResourceMethod method);

}

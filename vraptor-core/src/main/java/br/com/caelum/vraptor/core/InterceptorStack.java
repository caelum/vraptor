package br.com.caelum.vraptor.core;

import java.io.IOException;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

public interface InterceptorStack {

    void add(Interceptor interceptor);

    <T extends Interceptor> void add(Class<T> interceptor);

    void next(ResourceMethod method, Object resourceInstance) throws IOException, InterceptionException;

}

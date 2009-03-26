package br.com.caelum.vraptor;

import java.io.IOException;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;

public interface Interceptor {

	void intercept(InterceptorStack invocation, ResourceMethod method, Object resourceInstance) throws IOException, InterceptionException;

}

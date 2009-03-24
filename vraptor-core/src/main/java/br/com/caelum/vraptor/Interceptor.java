package br.com.caelum.vraptor;

import br.com.caelum.vraptor.core.InterceptorStack;

public interface Interceptor {

	void intercept(InterceptorStack invocation);

}

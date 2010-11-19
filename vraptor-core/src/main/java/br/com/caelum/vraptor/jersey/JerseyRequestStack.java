package br.com.caelum.vraptor.jersey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.DefaultRequestExecution;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.extra.ForwardToDefaultViewInterceptor;
import br.com.caelum.vraptor.interceptor.DeserializingInterceptor;
import br.com.caelum.vraptor.interceptor.ExceptionHandlerInterceptor;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.FlashInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.OutjectResult;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.interceptor.download.DownloadInterceptor;
import br.com.caelum.vraptor.interceptor.multipart.MultipartInterceptor;

public class JerseyRequestStack implements RequestExecution {

	private static final Logger LOG = LoggerFactory
			.getLogger(JerseyRequestStack.class);

	private final InterceptorStack interceptorStack;

	public JerseyRequestStack(InterceptorStack interceptorStack) {
		this.interceptorStack = interceptorStack;
	}

	public void execute() throws InterceptionException {
		LOG.debug("executing stack  DefaultRequestExecution");

		interceptorStack.add(MultipartInterceptor.class);
		interceptorStack.add(JerseyResourceLookupInterceptor.class);
		interceptorStack.add(FlashInterceptor.class);
		interceptorStack.add(InterceptorListPriorToExecutionExtractor.class);
		interceptorStack.add(InstantiateInterceptor.class);
		interceptorStack.add(ParametersInstantiatorInterceptor.class);
		interceptorStack.add(DeserializingInterceptor.class);
		interceptorStack.add(ExceptionHandlerInterceptor.class);
		interceptorStack.add(ExecuteMethodInterceptor.class);
		interceptorStack.add(OutjectResult.class);
		interceptorStack.add(DownloadInterceptor.class);
		interceptorStack.add(ForwardToDefaultViewInterceptor.class);
		interceptorStack.next(null, null);
	}
}

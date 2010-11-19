package br.com.caelum.vraptor.jersey;

import java.io.IOException;

import javax.servlet.ServletException;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.resource.MethodNotAllowedHandler;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.ResourceNotFoundHandler;

public class JerseyResourceLookupInterceptor implements Interceptor {

	private final RequestInfo info;
	private final Jersey jersey;
	private final ResourceLookupInterceptor delegate;

	public JerseyResourceLookupInterceptor(RequestInfo info, Jersey jersey,
			MethodNotAllowedHandler methodNotAllowedHandler,
			ResourceNotFoundHandler resourceNotFoundHandler,
			MethodInfo methodInfo, UrlToResourceTranslator translator) {
		this.info = info;
		this.jersey = jersey;
		this.delegate = new ResourceLookupInterceptor(translator, methodInfo,
				resourceNotFoundHandler, methodNotAllowedHandler, info);
	}

	public void intercept(InterceptorStack stack,
			ResourceMethod ignorableMethod, Object resourceInstance)
			throws InterceptionException {
		try {
			JerseyResourceComponentMethod m = jersey.findComponent(stack, info.getRequest(), info.getResponse());
			if (m == null) {
				delegate.intercept(stack, ignorableMethod, resourceInstance);
			} else {
				stack.next(m, resourceInstance);
			}
		} catch (ServletException e) {
			throw new InterceptionException("Error while looking up jersey",e);
		} catch (IOException e) {
			throw new InterceptionException("Error while looking up jersey",e);
		}
	}

	public boolean accepts(ResourceMethod method) {
		return true;
	}
}

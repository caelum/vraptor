
package br.com.caelum.vraptor.interceptor;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.ResourceNotFoundHandler;

/**
 * Looks up the resource for a specific request and delegates for the 404
 * component if unable to find it.
 * 
 * @author Guilherme Silveira
 * @author Cecilia
 */
public class ResourceLookupInterceptor implements Interceptor {

	private final UrlToResourceTranslator translator;
	private final MethodInfo requestInfo;
	private final RequestInfo request;
	private final ResourceNotFoundHandler resourceNotFoundHandler;

	public ResourceLookupInterceptor(UrlToResourceTranslator translator, MethodInfo requestInfo,
			ResourceNotFoundHandler resourceNotFoundHandler, RequestInfo request) {
		this.translator = translator;
		this.requestInfo = requestInfo;
		this.request = request;
		this.resourceNotFoundHandler = resourceNotFoundHandler;
	}

	public void intercept(InterceptorStack invocation, ResourceMethod ignorableMethod, Object resourceInstance)
			throws InterceptionException {
		ResourceMethod method = translator.translate(request.getRequest());
		if (method == null) {
			resourceNotFoundHandler.couldntFind(request);
			return;
		}
		requestInfo.setResourceMethod(method);
		invocation.next(method, resourceInstance);
	}

	public boolean accepts(ResourceMethod method) {
		return true;
	}

}

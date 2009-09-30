
package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.PageResult;

/**
 * The vraptor 2 compatible view interceptor.
 * 
 * @author guilherme silveira
 */
public class ViewInterceptor implements Interceptor {

	private final PageResult result;
	private final ComponentInfoProvider info;

	public ViewInterceptor(PageResult result, ComponentInfoProvider info) {
		this.result = result;
		this.info = info;
	}

	public boolean accepts(ResourceMethod method) {
		return true;
	}

	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
			throws InterceptionException {
		boolean vraptor2 = Info.isOldComponent(method.getResource());
		if(vraptor2) {
			if (info.shouldShowView(method)) {
				this.result.forward();
			}
		} else {
			stack.next(method, resourceInstance);
		}
	}

}

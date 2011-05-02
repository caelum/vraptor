/**
 * 
 */
package br.com.caelum.vraptor.http.iogi;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;

final class RequestAttributeInstantiator implements Instantiator<Object> {
	private final HttpServletRequest request;

	public RequestAttributeInstantiator(HttpServletRequest request) {
		this.request = request;
	}
	public Object instantiate(Target<?> target, Parameters params) {
		return request.getAttribute(target.getName());
	}

	public boolean isAbleToInstantiate(Target<?> target) {
		return request.getAttribute(target.getName()) != null;
	}

}
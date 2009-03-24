package br.com.caelum.vraptor.ioc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.core.Request;
import br.com.caelum.vraptor.resource.ResourceMethod;

public interface Container {

	<T> T withA(Class<T> type);

	void start();

	void stop();

	/**
	 * Prepares a request execution for this request/response pair.
	 */
	Request prepareFor(ResourceMethod method, HttpServletRequest request, HttpServletResponse response);

}

package br.com.caelum.vraptor.ioc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.core.Request;

public interface Container {

	<T> T withA(Class<T> type);

	void start();

	void stop();

	/**
	 * Prepares a request execution for this request/response pair.
	 */
	Request prepareFor(HttpServletRequest request, HttpServletResponse response);

}

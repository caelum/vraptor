
package br.com.caelum.vraptor.core;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.http.MutableRequest;

/**
 * Simple wrapper for request, response and servlet context.
 * 
 * @author Fabio Kung
 * @author Guilherme Silveira
 */
public class RequestInfo {
	private final ServletContext servletContext;

	private final MutableRequest request;

	private final HttpServletResponse response;

	public RequestInfo(ServletContext servletContext, MutableRequest request, HttpServletResponse response) {
		this.servletContext = servletContext;
		this.request = request;
		this.response = response;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public MutableRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}
}

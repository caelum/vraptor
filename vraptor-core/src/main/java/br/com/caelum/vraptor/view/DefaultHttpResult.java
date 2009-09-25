/**
 *
 */
package br.com.caelum.vraptor.view;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * Implementation that delegates to HttpServletResponse
 * @author Lucas Cavalcanti
 */
public class DefaultHttpResult implements HttpResult {

	private final HttpServletResponse response;

	public DefaultHttpResult(HttpServletResponse response) {
		this.response = response;
	}

	public HttpResult addDateHeader(String name, long date) {
		response.addDateHeader(name, date);
		return this;
	}

	public HttpResult addHeader(String name, String value) {
		response.addHeader(name, value);
		return this;
	}

	public HttpResult addIntHeader(String name, int value) {
		response.addIntHeader(name, value);
		return this;
	}

	public void sendError(int statusCode) {
		try {
			response.sendError(statusCode);
		} catch (IOException e) {
			throw new ResultException("Error while setting status code", e);
		}

	}

	public HttpResult setStatusCode(int statusCode) {
		response.setStatus(statusCode);
		return this;
	}

}

package br.com.caelum.vraptor.view;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.View;

/**
 * A view that deals with HTTP protocol, sending status and error codes
 * and adding headers.
 *
 * All methods of this interface have the same meaning as the corresponding
 * methods on {@link HttpServletResponse}
 * @author Lucas Cavalcanti
 *
 */
public interface HttpResult extends View {

	void sendError(int statusCode);

	HttpResult setStatusCode(int statusCode);

	HttpResult addHeader(String name, String value);

	HttpResult addIntHeader(String name, int value);

	HttpResult addDateHeader(String name, long date);

}

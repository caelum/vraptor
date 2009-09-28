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


	/**
	 * Send redirect with Moved Permanently Header
	 * Example:
	 * result.use(http()).movedPermanentlyTo("/clients");
	 * will move to /<contextPath>/clients
	 *
	 * @param uri absolute uri to redirect
	 */
	void movedPermanentlyTo(String url);

	/**
	 * same as movedPermanentlyTo(String), but will use
	 * the url for controller.method(args);
	 *
	 * Example:
	 * result.use(http()).movedPermanentlyTo(ClientsController.class).list();
	 */
	<T> T movedPermanentlyTo(Class<T> controller);
}

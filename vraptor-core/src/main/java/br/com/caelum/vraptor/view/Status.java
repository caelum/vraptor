package br.com.caelum.vraptor.view;

import java.util.EnumSet;

import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.resource.HttpMethod;

/**
 * Allows header related results.
 * 
 * @author guilherme silveira
 * @since 3.0.3
 */
public interface Status extends View {

	public void notFound();

	void header(String key, String value);

	void created();

	/**
	 * Sets the header to 201 and sets the location to the server's location +
	 * the location content.<br>
	 * created("/order/2") ==> http://localhost:8080/my_context/order/2
	 * 
	 * @param location
	 */
	void created(String location);

	void ok();

	void conflict();

	void methodNotAllowed(EnumSet<HttpMethod> allowedMethods);

	/**
	 * Send redirect with Moved Permanently Header Example:
	 * result.use(http()).movedPermanentlyTo("/clients"); will move to
	 * /<contextPath>/clients
	 * 
	 * @param uri
	 *            absolute uri to redirect
	 */
	void movedPermanentlyTo(String url);

	/**
	 * same as movedPermanentlyTo(String), but will use the url for
	 * controller.method(args);
	 * 
	 * Example:
	 * result.use(http()).movedPermanentlyTo(ClientsController.class).list();
	 */
	<T> T movedPermanentlyTo(Class<T> controller);

	/**
	 * The media type sent to the server is not supported.
	 * 
	 * @param errorMessage
	 */
	void unsupportedMediaType(String errorMessage);

	/**
	 * Notifies the client that he should not try to execute the same request
	 * again: part of its representation is somehow invalid.
	 * 
	 * @param message
	 */
	void badRequest(String message);
}

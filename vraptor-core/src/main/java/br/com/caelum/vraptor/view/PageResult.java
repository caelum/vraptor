
package br.com.caelum.vraptor.view;

import br.com.caelum.vraptor.View;

/**
 * A common forward/redirect/include page result.
 *
 * @author Guilherme Silveira
 */
public interface PageResult extends View {

	/**
	 * Server side forwarding to a result.
	 */
	void forward();

	/**
	 * Server side forwarding to a specific url.
	 */
	void forward(String url);

	/**
	 * Server side include a result.
	 */
	void include();

	/**
	 * Client side redirect to an specific url.
	 *
	 * @param url
	 */
	void redirect(String url);

	/**
	 * Render the default view of given logic, without executing the logic.
	 * Ex: result.use(page()).of(ClientsController.class).list();
	 * will render the view /WEB-INF/jsp/clients/list.jsp without calling list method.
	 */
	<T> T of(Class<T> controllerType);

}
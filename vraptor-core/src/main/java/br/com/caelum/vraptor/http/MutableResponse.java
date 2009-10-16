package br.com.caelum.vraptor.http;

import javax.servlet.http.HttpServletResponse;

/**
 * A response that can add listeners to Redirects
 * @author Lucas Cavalcanti
 * @author Caires Vinicius
 * @author Adriano Almeida
 */
public interface MutableResponse extends HttpServletResponse {

	void addRedirectListener(RedirectListener listener);

	interface RedirectListener {
		void beforeRedirect();
	}
}

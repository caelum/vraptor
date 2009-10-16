package br.com.caelum.vraptor.http;

public interface MutableResponse {

	void addRedirectListener(RedirectListener listener);

	interface RedirectListener {
		void beforeRedirect();
	}
}

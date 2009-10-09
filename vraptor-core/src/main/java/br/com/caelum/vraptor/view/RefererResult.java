package br.com.caelum.vraptor.view;

import br.com.caelum.vraptor.View;

/**
 * Redirects or forwards to the Referer.
 *
 * "The Referer[sic] request-header field allows the client to specify,
 * 	for the server's benefit, the address (URI) of the resource from which
 * 	the Request-URI was obtained (the "referrer", although the header field
 * 	is misspelled.)"
 *  -- http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
 *
 * As Referer is not a mandatory header, you can specify a fallback result, to be used
 * when referrer is not specified:
 * result.use(referer()).redirect().or(logic()).redirectTo(AnyController.class).anyMethod();
 *
 * @author Lucas Cavalcanti
 *
 */
public interface RefererResult {

	/**
	 * Forwards to Referer.
	 * @return
	 */
	FallbackResult forward();

	/**
	 * Redirects to Referer.
	 * @return
	 */
	FallbackResult redirect();

	interface FallbackResult {
		/**
		 * Use given view if Referer Header was not set.
		 * @param <T>
		 * @param view
		 * @return
		 */
		<T extends View> T or(Class<T> view);
	}
}

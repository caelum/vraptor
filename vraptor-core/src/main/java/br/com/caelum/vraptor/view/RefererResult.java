package br.com.caelum.vraptor.view;


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
 *
 * try {
 * 	result.use(referer()).redirect();
 * } catch (IllegalStateException e) {
 * 	result.use(logic()).redirectTo(AnyController.class).anyMethod();
 * }
 *
 *
 * @author Lucas Cavalcanti
 *
 */
public interface RefererResult {

	/**
	 * Forwards to Referer.
	 * @throws IllegalStateException when there is no Referer header on request
	 */
	void forward() throws IllegalStateException;

	/**
	 * Redirects to Referer.
	 * @throws IllegalStateException when there is no Referer header on request
	 */
	void redirect() throws IllegalStateException;

}

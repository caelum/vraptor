/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
public interface RefererResult extends View {

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

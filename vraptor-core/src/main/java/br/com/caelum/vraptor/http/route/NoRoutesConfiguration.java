
package br.com.caelum.vraptor.http.route;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * No extra routes are registered.
 * 
 * @author guilherme silveira
 */
@ApplicationScoped
public class NoRoutesConfiguration implements RoutesConfiguration {

	public NoRoutesConfiguration() {
	}

	public void config(Router router) {
	}

}

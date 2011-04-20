package br.com.caelum.vraptor.view;

import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Component responsible to allow redirects with the same passed parameters
 *
 * @author Lucas Cavalcanti
 * @since 3.3.2
 *
 */
public interface FlashScope {
	void includeParameters(ResourceMethod method, Object[] args);
	Object[] consumeParameters(ResourceMethod method);
}

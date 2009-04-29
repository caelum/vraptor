package br.com.caelum.vraptor.http;

import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Handles different rules in order to translate urls into resource methods.
 * 
 * @author Guilherme Silveira
 */
public interface LocatorRules {

	void add(Rules rules);

	ResourceMethod parse(String uri);

}

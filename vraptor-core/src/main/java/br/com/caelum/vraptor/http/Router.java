package br.com.caelum.vraptor.http;

import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Handles different rules in order to translate urls into resource methods.
 * 
 * @author Guilherme Silveira
 */
public interface Router {

	void add(Rules rules);

	ResourceMethod parse(String uri, HttpMethod method);

}

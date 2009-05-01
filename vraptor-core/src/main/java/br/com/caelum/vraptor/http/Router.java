package br.com.caelum.vraptor.http;

import java.util.Set;

import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Handles different rules in order to translate urls into resource methods.
 * 
 * @author Guilherme Silveira
 */
public interface Router {

	void add(ListOfRules rules);

	ResourceMethod parse(String uri, HttpMethod method, MutableRequest request);

    Set<Resource> all();

    /**
     * Registers this resource using the default algorithm for uri identification.
     */
	void register(Resource resource);

}

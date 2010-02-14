package br.com.caelum.vraptor.restfulie;

import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;

/**
 * Handles extra handlers when rendering a resource.
 * 
 * @author guilherme silveira
 */
public interface RestHeadersHandler {
	
	void handle(HypermediaResource object);

}

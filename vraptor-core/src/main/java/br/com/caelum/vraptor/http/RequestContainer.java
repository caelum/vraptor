package br.com.caelum.vraptor.http;

import br.com.caelum.vraptor.resource.DefaultResourceInstance;


/**
 * A container related to an specific request/response.
 * 
 * @author Guilherme Silveira
 */
public interface RequestContainer {

	<T> T withA(Class<T> type);

	void register(Object instance);

}

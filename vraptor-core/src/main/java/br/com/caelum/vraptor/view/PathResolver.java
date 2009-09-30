
package br.com.caelum.vraptor.view;

import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Decides the url to go to after a request was executed.
 * 
 * @author Guilherme Silveira
 */
public interface PathResolver {

    String pathFor(ResourceMethod method);

}

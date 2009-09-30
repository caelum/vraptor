
package br.com.caelum.vraptor.resource;

import br.com.caelum.vraptor.core.RequestInfo;

/**
 * A component capable of handling not found components.
 * @author Guilherme Silveira
 * @author Cecilia
 */
public interface ResourceNotFoundHandler {

	public void couldntFind (RequestInfo request);
}


package br.com.caelum.vraptor.http.route;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.resource.ResourceClass;

/**
 * Generates no routes for any resource. Only allows configurarion through the use of custom routes.
 * @author guilherme silveira
 *
 */
public class NoRoutesParser implements RoutesParser {

	private static final List<Route> EMPTY = new ArrayList<Route>();

	public List<Route> rulesFor(ResourceClass resource) {
		return EMPTY;
	}

}

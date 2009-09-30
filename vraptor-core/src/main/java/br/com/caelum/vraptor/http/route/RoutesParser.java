
package br.com.caelum.vraptor.http.route;

import java.util.List;

import br.com.caelum.vraptor.resource.ResourceClass;

/**
 * Extracts all possible routes for this specific resource.
 *
 * @author guilherme silveira
 */
public interface RoutesParser {

    List<Route> rulesFor(ResourceClass resource);

}

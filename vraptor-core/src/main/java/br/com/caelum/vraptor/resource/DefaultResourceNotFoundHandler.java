
package br.com.caelum.vraptor.resource;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Default 404 component
 *
 * @author guilherme silveira
 */
@ApplicationScoped
public class DefaultResourceNotFoundHandler implements ResourceNotFoundHandler {

	public void couldntFind(RequestInfo request) {
		try {
			HttpServletResponse response = request.getResponse();
			response.sendError(404);
		} catch (IOException e) {
			throw new InterceptionException(e);
		}
	}

}

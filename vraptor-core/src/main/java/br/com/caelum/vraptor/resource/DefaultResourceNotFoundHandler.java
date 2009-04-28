package br.com.caelum.vraptor.resource;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.VRaptorRequest;

public class DefaultResourceNotFoundHandler implements ResourceNotFoundHandler {

	public void couldntFind(VRaptorRequest request) {
        try {
        	HttpServletResponse response = request.getResponse();
        	response.setStatus(404);
			response.getWriter().println("resource not found");
		} catch (IOException e) {
            throw new InterceptionException(e);
		}
	}

}

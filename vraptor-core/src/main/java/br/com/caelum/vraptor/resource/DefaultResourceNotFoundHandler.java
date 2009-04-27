package br.com.caelum.vraptor.resource;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.InterceptionException;

public class DefaultResourceNotFoundHandler implements ResourceNotFoundHandler {

	public void couldntFind(HttpServletResponse response) {
        try {
        	response.setStatus(404);
			response.getWriter().println("resource not found");
		} catch (IOException e) {
            throw new InterceptionException(e);
		}
	}

}

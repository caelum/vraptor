package br.com.caelum.vraptor.resource;

import javax.servlet.http.HttpServletResponse;

public interface ResourceNotFoundHandler {

	public void couldntFind (HttpServletResponse response);
}

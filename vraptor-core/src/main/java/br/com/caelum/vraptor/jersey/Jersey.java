package br.com.caelum.vraptor.jersey;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.core.InterceptorStack;

public interface Jersey {

	@SuppressWarnings("unchecked")
	public abstract JerseyResourceComponentMethod findComponent(
			InterceptorStack stack, HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException;

}
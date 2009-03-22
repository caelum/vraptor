package br.com.caelum.vraptor.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.resource.ResourceMethod;

public class RequestExecution implements Request {

	private final HttpServletRequest request;
	private final HttpServletResponse response;

	public RequestExecution(HttpServletRequest request,
			HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public void execute(ResourceMethod method) throws IOException {
		PrintWriter out = response.getWriter();
		out.println("executing resource " + method);
	}

}

package br.com.caelum.vraptor.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.http.RequestContainer;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * A request execution process. Uses a container with REQUEST scope objects.
 * 
 * @author Guilherme Silveira
 */
public class RequestExecution implements Request {

	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final RequestContainer container;

	public RequestExecution(HttpServletRequest request,
			HttpServletResponse response, RequestContainer container) {
		this.request = request;
		this.response = response;
		this.container = container;
	}

	public void execute(ResourceMethod method) throws IOException {
		PrintWriter out = response.getWriter();
		out.println("executing resource " + method);
		InterceptorStack stack = container.withA(InterceptorStack.class);
		stack.add(container.withA(InstantiateInterceptor.class));
		stack.next();
	}

}

package br.com.caelum.vraptor.jersey;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.server.impl.model.method.dispatch.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

public class MyResourceMethodDispatchProvider implements ResourceMethodDispatchProvider{

	public RequestDispatcher create(
			AbstractResourceMethod abstractResourceMethod) {
		return null;
	}

}

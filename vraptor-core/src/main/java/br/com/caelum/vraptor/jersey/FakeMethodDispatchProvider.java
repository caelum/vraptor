package br.com.caelum.vraptor.jersey;

import java.util.Set;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.server.impl.application.DispatcherFactory;
import com.sun.jersey.server.impl.model.method.dispatch.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

/**
 * A vraptor based method dispatcher instead of invoking the method it will just
 * save its information in the context for a second invocation.
 * 
 * @author guilherme silveira
 */
public class FakeMethodDispatchProvider implements
		ResourceMethodDispatchProvider {

	private final DispatcherFactory delegate;

	public FakeMethodDispatchProvider(DispatcherFactory delegate) {
		this.delegate = delegate;
	}

	public RequestDispatcher create(final AbstractResourceMethod method) {
		return new JerseyDispatcher(delegate, method);
	}

}

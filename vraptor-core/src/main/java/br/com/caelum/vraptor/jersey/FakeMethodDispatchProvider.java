package br.com.caelum.vraptor.jersey;



import com.sun.jersey.api.model.AbstractResourceMethod;
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

	public static final String METHOD_TO_EXECUTE = FakeMethodDispatchProvider.class.getPackage() + ".method";
	public static final String RESOURCE_TO_USE = FakeMethodDispatchProvider.class.getPackage() + ".resource";

	public RequestDispatcher create(final AbstractResourceMethod method) {
		return new JerseyDispatcher(method);
	}

}

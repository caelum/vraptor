package br.com.caelum.vraptor;

import java.lang.reflect.Method;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.SimpleNode;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.internal.ExpectationBuilder;
import org.jmock.lib.legacy.ClassImposteriser;

import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class VRaptorMockery {

	private int count = 0;

	private Mockery mockery;

	public VRaptorMockery() {
		this(false);
	}

	public VRaptorMockery(boolean supportConcreteClasses) {
		mockery = new Mockery();
		if (supportConcreteClasses) {
			mockery.setImposteriser(ClassImposteriser.INSTANCE);
		}
	}

	public void assertIsSatisfied() {
		mockery.assertIsSatisfied();
	}

	public void checking(ExpectationBuilder expectations) {
		mockery.checking(expectations);
	}

	public <T> T mock(Class<T> typeToMock) {
		return mockery.mock(typeToMock);
	}

	public ResourceMethod method(final Method declaredMethod) {
		final ResourceMethod method = mockery.mock(ResourceMethod.class);
		checking(new Expectations() {
			{
				one(method).getMethod();
				will(returnValue(declaredMethod));
			}
		});
		return method;
	}

	public <T> Resource resource(final Class<T> type) {
		final Resource resource = mockery.mock(Resource.class, "resource : " + type + (++count));
		mockery.checking(new Expectations() {
			{
				allowing(resource).getType();
				will(returnValue(type));
			}
		});
		return resource;
	}

	public <T> Container container(final Class<T> type, final T object) {
		final Container container = mockery.mock(Container.class);
		mockery.checking(new Expectations() {
			{
				one(container).instanceFor(type);
				will(returnValue(object));
			}
		});
		return container;
	}

	public <T> ResourceMethod methodForResource(Class<T> type) {
		final Resource resource = resource(type);
		final ResourceMethod method = mockery.mock(ResourceMethod.class);
		checking(new Expectations() {
			{
				one(method).getResource();
				will(returnValue(resource));
			}
		});
		return method;
	}

	public <T> ResourceMethod methodFor(final Class<T> type, final String methodName, final Class<?>... params)
			throws NoSuchMethodException {
		final Resource resource = mockery.mock(Resource.class);
		mockery.checking(new Expectations() {
			{
				allowing(resource).getType();
				will(returnValue(type));
			}
		});
		final ResourceMethod method = mockery.mock(ResourceMethod.class);
		checking(new Expectations() {
			{
				allowing(method).getResource();
				will(returnValue(resource));
				allowing(method).getMethod();
				will(returnValue(type.getDeclaredMethod(methodName, params)));
			}
		});
		return method;
	}

	public Localization localization() {
		final Localization loc = mockery.mock(Localization.class);
		final ResourceBundle bundle = ResourceBundle.getBundle("messages");
		mockery.checking(new Expectations() {
			{
				allowing(loc).getBundle();
				will(returnValue(bundle));
			}
		});
		return loc;
	}

	/**
	 * Creates a "simplenode" from ognl which returns the desired toString.
	 */
	public SimpleNode ognlNode(final String name) {
		// ognl design sucks when methods should return the interface types, not the implementation types
		return new SimpleNode(0) {
			private static final long serialVersionUID = 1L;
			protected Object getValueBody(OgnlContext arg0, Object arg1) throws OgnlException {
				return null;
			}
			public String toString() {
				return name;
			}
		};

	}

	public HttpServletRequest request() {
		return mockery.mock(HttpServletRequest.class);
	}

}

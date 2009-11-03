package br.com.caelum.vraptor.core;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class InstantiatedInterceptorHandlerTest {


	private Mockery mockery;
	private Interceptor interceptor;
	private InstantiatedInterceptorHandler handler;
	private InterceptorStack stack;
	private ResourceMethod method;

	@Before
	public void setUp() throws Exception {
		this.mockery = new Mockery();
		this.interceptor = mockery.mock(Interceptor.class);
		this.stack = mockery.mock(InterceptorStack.class);
		this.method = mockery.mock(ResourceMethod.class);
		this.handler = new InstantiatedInterceptorHandler(interceptor);
	}

	@Test
	public void shouldExecuteInterceptorIfItAcceptsMethod() throws Exception {

		mockery.checking(new Expectations() {
			{
				one(interceptor).accepts(method); will(returnValue(true));
				one(interceptor).intercept(stack, method, null);
				never(stack).next(method, null);
			}
		});

		handler.execute(stack, method, null);
		mockery.assertIsSatisfied();
	}
	@Test
	public void shouldContinueStackIfInterceptorDoesntAcceptMethod() throws Exception {

		mockery.checking(new Expectations() {
			{
				one(interceptor).accepts(method); will(returnValue(false));
				never(interceptor).intercept(stack, method, null);
				one(stack).next(method, null);
			}
		});

		handler.execute(stack, method, null);
		mockery.assertIsSatisfied();
	}
}

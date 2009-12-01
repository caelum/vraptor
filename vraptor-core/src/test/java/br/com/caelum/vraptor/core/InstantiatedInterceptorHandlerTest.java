package br.com.caelum.vraptor.core;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class InstantiatedInterceptorHandlerTest {

	@Mock private Interceptor interceptor;
	@Mock private InterceptorStack stack;
	@Mock private ResourceMethod method;
	private InstantiatedInterceptorHandler handler;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.handler = new InstantiatedInterceptorHandler(interceptor);
	}

	@Test
	public void shouldExecuteInterceptorIfItAcceptsMethod() throws Exception {
		when(interceptor.accepts(method)).thenReturn(true);

		handler.execute(stack, method, null);

		verify(interceptor).intercept(stack, method, null);
		verify(stack, never()).next(method, null);
	}

	@Test
	public void shouldContinueStackIfInterceptorDoesntAcceptMethod() throws Exception {
		when(interceptor.accepts(method)).thenReturn(false);

		handler.execute(stack, method, null);

		verify(interceptor, never()).intercept(stack, method, null);
		verify(stack).next(method, null);
	}
}

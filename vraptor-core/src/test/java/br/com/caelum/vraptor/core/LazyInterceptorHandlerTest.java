package br.com.caelum.vraptor.core;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Lazy;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class LazyInterceptorHandlerTest {

	private @Mock Container container;
	private @Mock InterceptorStack stack;
	private @Mock ResourceMethod method;
	private @Mock Object instance;
	private @Mock Interceptor mockInterceptor;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(container.instanceFor(any(Class.class))).thenReturn(mockInterceptor);
	}

	@Test
	public void shouldUseContainerIfInterceptorAccepts() throws Exception {
		LazyInterceptorHandler handler = new LazyInterceptorHandler(container,AlwaysAcceptInterceptor.class);

		handler.execute(stack,method,instance);

		verify(container).instanceFor(AlwaysAcceptInterceptor.class);
		verify(mockInterceptor).intercept(stack, method, instance);
		verify(mockInterceptor, never()).accepts(any(ResourceMethod.class));
	}
	@Test
	public void shouldNotUseContainerIfInterceptorDoesntAccept() throws Exception {
		LazyInterceptorHandler handler = new LazyInterceptorHandler(container, NeverAcceptInterceptor.class);

		handler.execute(stack,method,instance);

		verify(container, never()).instanceFor(any(Class.class));
		verify(stack).next(method, instance);
	}

	@Test(expected=InterceptionException.class)
	public void shouldFailIfUsingConstructorParametersOnConstructor() throws Exception {
		LazyInterceptorHandler handler = new LazyInterceptorHandler(container, InterceptorUsingConstructorParameters.class);

		handler.execute(stack,method,instance);
	}
	@Test(expected=InterceptionException.class)
	public void shouldFailIfUsingConstructorParametersOnAcceptsMethod() throws Exception {
		LazyInterceptorHandler handler = new LazyInterceptorHandler(container, InterceptorUsingConstructorParametersOnAccepts.class);

		handler.execute(stack,method,instance);
	}

	@Lazy
	static class AlwaysAcceptInterceptor implements Interceptor {
		public AlwaysAcceptInterceptor(String xxxx) {
		}
		public boolean accepts(ResourceMethod method) {
			return true;
		}
		public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance){}
	}

	@Lazy
	private static class NeverAcceptInterceptor implements Interceptor {
		public boolean accepts(ResourceMethod method) {
			return false;
		}
		public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance){}
	}

	@Lazy
	static class InterceptorUsingConstructorParameters implements Interceptor {
		public InterceptorUsingConstructorParameters(String xuxu) {
			xuxu.toString();
		}
		public boolean accepts(ResourceMethod method) {
			return false;
		}
		public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance){}
	}
	@Lazy
	static class InterceptorUsingConstructorParametersOnAccepts implements Interceptor {
		private final String xuxu;
		public InterceptorUsingConstructorParametersOnAccepts(String xuxu) {
			this.xuxu = xuxu;
		}
		public boolean accepts(ResourceMethod method) {
			return xuxu.contains("o'really?");
		}
		public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance){}
	}

}

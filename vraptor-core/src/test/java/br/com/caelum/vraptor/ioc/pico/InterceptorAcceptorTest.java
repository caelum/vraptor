package br.com.caelum.vraptor.ioc.pico;

import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.interceptor.InterceptorSequence;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class InterceptorAcceptorTest {

	private InterceptorAcceptor acceptor;
	private Mockery mockery;
	private InterceptorRegistry registry;

	@Before
	public void setup() {
		this.mockery = new Mockery();
		this.registry = mockery.mock(InterceptorRegistry.class);
		this.acceptor = new InterceptorAcceptor(registry);
	}

	@Intercepts
	class IgnorableIntercepts {
	}

	@Test
	public void shouldIgnoreInterceptsWithAFailingType() {
		mockery.checking(new Expectations() {
			{
				never(registry).register(with(any(List.class)));
			}
		});
		acceptor.analyze(IgnorableIntercepts.class);
		mockery.assertIsSatisfied();
	}

	@Intercepts
	public static class MySequence implements InterceptorSequence {
		@SuppressWarnings("unchecked")
		public Class<? extends Interceptor>[] getSequence() {
			return new Class[] { InterceptorAnnotated.class, InterceptorNotAnnotated.class };
		}
	}

	@Test
	public void shouldAddAllInterceptorsFromASequenceInItsOrder() {

		mockery.checking(new Expectations() {
			{
				one(registry).register(Arrays.asList(InterceptorAnnotated.class, InterceptorNotAnnotated.class));
			}
		});
		acceptor.analyze(MySequence.class);
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldAcceptInterceptorsAnnotatedWithInterceptorAnnotation() {
		mockery.checking(new Expectations() {
			{
				one(registry).register(Arrays.<Class<? extends Interceptor>> asList(InterceptorAnnotated.class));
			}
		});
		acceptor.analyze(InterceptorAnnotated.class);
		mockery.assertIsSatisfied();
	}

	@Test
	public void ignoresNonAnnotatedInterceptors() {
		mockery.checking(new Expectations() {
			{
				never(registry).register(Arrays.<Class<? extends Interceptor>> asList(InterceptorNotAnnotated.class));
			}
		});
		acceptor.analyze(InterceptorNotAnnotated.class);
		mockery.assertIsSatisfied();
	}

	@Intercepts
	class InterceptorAnnotated implements Interceptor {
		public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
				throws InterceptionException {
		}

		public boolean accepts(ResourceMethod method) {
			return true;
		}
	}

	class InterceptorNotAnnotated implements Interceptor {
		public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
				throws InterceptionException {
		}

		public boolean accepts(ResourceMethod method) {
			return true;
		}
	}
}

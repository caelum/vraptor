package br.com.caelum.vraptor.util.hibernate.extra;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class ParameterLoaderInterceptorTest {


	private @Mock Session session;
	private @Mock HttpServletRequest request;
	private @Mock ParameterNameProvider provider;
	private @Mock Result result;
	private ParameterLoaderInterceptor interceptor;
	private ResourceMethod method;
	private @Mock InterceptorStack stack;
	private Object instance;
	private ResourceMethod managed;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		interceptor = new ParameterLoaderInterceptor(session, request, provider, result);
		method = DefaultResourceMethod.instanceFor(Resource.class, Resource.class.getMethod("method", Entity.class));
		managed = DefaultResourceMethod.instanceFor(Resource.class, Resource.class.getMethod("managed", Entity.class));
	}

	@Test
	public void shouldLoadEntityUsingId() throws Exception {
		when(provider.parameterNamesFor(method.getMethod())).thenReturn(new String[] {"entity"});
		when(request.getParameter("entity.id")).thenReturn("123");
		Entity expectedEntity = new Entity();
		when(session.get(Entity.class, 123l)).thenReturn(expectedEntity);

		interceptor.intercept(stack, method, instance);

		verify(request).setAttribute("entity", expectedEntity);
		verify(session).evict(expectedEntity);
		verify(stack).next(method, instance);
	}

	@Test
	public void shouldNotEvictIfIsManaged() throws Exception {
		when(provider.parameterNamesFor(managed.getMethod())).thenReturn(new String[] {"entity"});
		when(request.getParameter("entity.id")).thenReturn("123");
		Entity expectedEntity = new Entity();
		when(session.get(Entity.class, 123l)).thenReturn(expectedEntity);

		interceptor.intercept(stack, managed, instance);

		verify(request).setAttribute("entity", expectedEntity);
		verify(session, never()).evict(expectedEntity);
		verify(stack).next(managed, instance);
	}

	@Test
	public void shouldSend404WhenNoIdIsSet() throws Exception {
		when(provider.parameterNamesFor(method.getMethod())).thenReturn(new String[] {"entity"});
		when(request.getParameter("entity.id")).thenReturn(null);

		interceptor.intercept(stack, method, instance);

		verify(request, never()).setAttribute(eq("entity"), any());
		verify(result).notFound();
		verify(stack, never()).next(method, instance);
	}

	@Test
	public void shouldSend404WhenIdDoesntExist() throws Exception {
		when(provider.parameterNamesFor(method.getMethod())).thenReturn(new String[] {"entity"});
		when(request.getParameter("entity.id")).thenReturn("123");
		when(session.get(Entity.class, 123l)).thenReturn(null);

		interceptor.intercept(stack, method, instance);

		verify(request, never()).setAttribute(eq("entity"), any());
		verify(result).notFound();
		verify(stack, never()).next(method, instance);
	}

	static class Entity {
		private Long id;
	}

	static class Resource {
		public void method(@Load Entity entity) {
		}
		public void managed(@Load(managed=true) Entity entity) {
		}
	}

}

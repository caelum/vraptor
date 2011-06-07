package br.com.caelum.vraptor.util.hibernate.extra;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.view.FlashScope;
import org.hamcrest.Matchers;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Stubber;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.converter.LongConverter;
import br.com.caelum.vraptor.converter.StringConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.util.test.MockLocalization;

public class ParameterLoaderInterceptorTest {


	private @Mock Session session;
	private @Mock HttpServletRequest request;
	private @Mock ParameterNameProvider provider;
	private @Mock Result result;
	private @Mock Converters converters;
    private @Mock FlashScope flash;

    private ParameterLoaderInterceptor interceptor;
    private ResourceMethod method;
    private @Mock InterceptorStack stack;
    private @Mock Object instance;
    private ResourceMethod other;
    private ResourceMethod noId;

    @Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		interceptor = new ParameterLoaderInterceptor(session, request, provider, result, converters, new MockLocalization(), flash);
		method = DefaultResourceMethod.instanceFor(Resource.class, Resource.class.getMethod("method", Entity.class));
		other = DefaultResourceMethod.instanceFor(Resource.class, Resource.class.getMethod("other", OtherEntity.class));
		noId = DefaultResourceMethod.instanceFor(Resource.class, Resource.class.getMethod("noId", NoIdEntity.class));

		when(converters.to(Long.class)).thenReturn(new LongConverter());
		when(converters.to(String.class)).thenReturn(new StringConverter());
	}

	@Test
	public void shouldLoadEntityUsingId() throws Exception {
		when(provider.parameterNamesFor(method.getMethod())).thenReturn(new String[] {"entity"});
		when(request.getParameter("entity.id")).thenReturn("123");
		Entity expectedEntity = new Entity();
		when(session.get(Entity.class, 123l)).thenReturn(expectedEntity);

		interceptor.intercept(stack, method, instance);

		verify(request).setAttribute("entity", expectedEntity);
		verify(stack).next(method, instance);
	}

	@Test
	public void shouldLoadEntityUsingIdOfAnyType() throws Exception {
		when(provider.parameterNamesFor(other.getMethod())).thenReturn(new String[] {"entity"});
		when(request.getParameter("entity.id")).thenReturn("123");
		OtherEntity expectedEntity = new OtherEntity();
		when(session.get(OtherEntity.class, "123")).thenReturn(expectedEntity);

		interceptor.intercept(stack, other, instance);

		verify(request).setAttribute("entity", expectedEntity);
		verify(stack).next(other, instance);
	}

	@Test
	public void shouldOverrideFlashScopedArgsIfAny() throws Exception {
		when(provider.parameterNamesFor(method.getMethod())).thenReturn(new String[] {"entity"});
		when(request.getParameter("entity.id")).thenReturn("123");
        Object[] args = {new Entity()};

        when(flash.consumeParameters(method)).thenReturn(args);

        Entity expectedEntity = new Entity();
		when(session.get(Entity.class, 123l)).thenReturn(expectedEntity);

		interceptor.intercept(stack, method, instance);

		assertThat(args[0], is((Object) expectedEntity));

		verify(stack).next(method, instance);
        verify(flash).includeParameters(method, args);
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

	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentIfEntityDoesntHaveId() throws Exception {
		when(provider.parameterNamesFor(noId.getMethod())).thenReturn(new String[] {"entity"});
		when(request.getParameter("entity.id")).thenReturn("123");
		fail().when(request).setAttribute(eq("entity"), any());
		fail().when(result).notFound();
		fail().when(stack).next(noId, instance);

		interceptor.intercept(stack, noId, instance);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentIfIdIsNotConvertable() throws Exception {
		when(provider.parameterNamesFor(method.getMethod())).thenReturn(new String[] {"entity"});
		when(request.getParameter("entity.id")).thenReturn("123");
		when(converters.to(Long.class)).thenReturn(null);
		fail().when(request).setAttribute(eq("entity"), any());
		fail().when(result).notFound();
		fail().when(stack).next(method, instance);

		interceptor.intercept(stack, method, instance);
	}


	static class Entity {
		private Long id;
	}
	static class OtherEntity {
		private String id;
	}
	static class NoIdEntity {
	}

	static class Resource {
		public void method(@Load Entity entity) {
		}
		public void other(@Load OtherEntity entity) {
		}
		public void noId(@Load NoIdEntity entity) {
		}
	}
	private Stubber fail() {
		return doThrow(new AssertionError());
	}

}

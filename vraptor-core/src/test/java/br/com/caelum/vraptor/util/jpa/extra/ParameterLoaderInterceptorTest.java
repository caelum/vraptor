package br.com.caelum.vraptor.util.jpa.extra;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
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
import br.com.caelum.vraptor.view.FlashScope;

public class ParameterLoaderInterceptorTest {


	private @Mock EntityManager em;
	private @Mock HttpServletRequest request;
	private @Mock ParameterNameProvider provider;
	private @Mock Result result;
	private @Mock Converters converters;
    private @Mock FlashScope flash;
    
    private @Mock Metamodel metamodel;
    private @Mock EntityType entityType;
    private @Mock Type type;
    private @Mock SingularAttribute attribute;

    private ParameterLoaderInterceptor interceptor;
    private @Mock InterceptorStack stack;
    private @Mock Object instance;
    private ResourceMethod method;
    private ResourceMethod methodOtherIdName;
    private ResourceMethod other;
    private ResourceMethod noId;

    @Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		interceptor = new ParameterLoaderInterceptor(em, request, provider, result, converters, new MockLocalization(), flash);
        method = DefaultResourceMethod.instanceFor(Resource.class, Resource.class.getMethod("method", Entity.class));
        methodOtherIdName = DefaultResourceMethod.instanceFor(Resource.class, Resource.class.getMethod("methodOtherIdName", EntityOtherIdName.class));
        other = DefaultResourceMethod.instanceFor(Resource.class, Resource.class.getMethod("other", OtherEntity.class));
        noId = DefaultResourceMethod.instanceFor(Resource.class, Resource.class.getMethod("noId", NoIdEntity.class));

		when(converters.to(Long.class)).thenReturn(new LongConverter());
		when(converters.to(String.class)).thenReturn(new StringConverter());
		
        when(em.getMetamodel()).thenReturn(metamodel);
        when(metamodel.entity(any(Class.class))).thenReturn(entityType);
        when(entityType.getIdType()).thenReturn(type);
	}
    
    @Test
    @SuppressWarnings({ "unchecked" })
	public void shouldLoadEntityUsingId() throws Exception {
		when(provider.parameterNamesFor(method.getMethod())).thenReturn(new String[] {"entity"});
		when(request.getParameter("entity.id")).thenReturn("123");
		Entity expectedEntity = new Entity();
		when(em.find(Entity.class, 123L)).thenReturn(expectedEntity);
		
        when(entityType.getDeclaredId(Long.class)).thenReturn(attribute);
        when(attribute.getName()).thenReturn("id");
        when(type.getJavaType()).thenReturn(Long.class);

		interceptor.intercept(stack, method, instance);

		verify(request).setAttribute("entity", expectedEntity);
		verify(stack).next(method, instance);
	}

    @Test
    public void shouldLoadEntityUsingOtherIdName() throws Exception {
        when(provider.parameterNamesFor(methodOtherIdName.getMethod())).thenReturn(new String[] {"entity"});
        when(request.getParameter("entity.otherIdName")).thenReturn("456");
        EntityOtherIdName expectedEntity = new EntityOtherIdName();
        when(em.find(EntityOtherIdName.class, 456L)).thenReturn(expectedEntity);
        
        when(entityType.getDeclaredId(Long.class)).thenReturn(attribute);
        when(attribute.getName()).thenReturn("otherIdName");
        when(type.getJavaType()).thenReturn(Long.class);

        interceptor.intercept(stack, methodOtherIdName, instance);

        verify(request).setAttribute("entity", expectedEntity);
        verify(stack).next(methodOtherIdName, instance);
    }
    
	@Test
	public void shouldLoadEntityUsingIdOfAnyType() throws Exception {
		when(provider.parameterNamesFor(other.getMethod())).thenReturn(new String[] {"entity"});
		when(request.getParameter("entity.id")).thenReturn("123");
		OtherEntity expectedEntity = new OtherEntity();
		when(em.find(OtherEntity.class, "123")).thenReturn(expectedEntity);

        when(entityType.getDeclaredId(String.class)).thenReturn(attribute);
        when(attribute.getName()).thenReturn("id");
        when(type.getJavaType()).thenReturn(String.class);
        
		interceptor.intercept(stack, other, instance);

		verify(request).setAttribute("entity", expectedEntity);
		verify(stack).next(other, instance);
	}

	@Test
	public void shouldOverrideFlashScopedArgsIfAny() throws Exception {
		when(provider.parameterNamesFor(method.getMethod())).thenReturn(new String[] {"entity"});
		when(request.getParameter("entity.id")).thenReturn("123");
        Object[] args = {new Entity()};

        when(entityType.getDeclaredId(Long.class)).thenReturn(attribute);
        when(attribute.getName()).thenReturn("id");
        when(type.getJavaType()).thenReturn(Long.class);
        
        when(flash.consumeParameters(method)).thenReturn(args);

        Entity expectedEntity = new Entity();
		when(em.find(Entity.class, 123l)).thenReturn(expectedEntity);

		interceptor.intercept(stack, method, instance);

		assertThat(args[0], is((Object) expectedEntity));

		verify(stack).next(method, instance);
        verify(flash).includeParameters(method, args);
	}

	@Test
	public void shouldSend404WhenNoIdIsSet() throws Exception {
		when(provider.parameterNamesFor(method.getMethod())).thenReturn(new String[] {"entity"});
		when(request.getParameter("entity.id")).thenReturn(null);

        when(entityType.getDeclaredId(Long.class)).thenReturn(attribute);
        when(attribute.getName()).thenReturn("id");
        when(type.getJavaType()).thenReturn(Long.class);
        
		interceptor.intercept(stack, method, instance);

		verify(request, never()).setAttribute(eq("entity"), any());
		verify(result).notFound();
		verify(stack, never()).next(method, instance);
	}

	@Test
	public void shouldSend404WhenIdDoesntExist() throws Exception {
		when(provider.parameterNamesFor(method.getMethod())).thenReturn(new String[] {"entity"});
		when(request.getParameter("entity.id")).thenReturn("123");
		when(em.find(Entity.class, 123l)).thenReturn(null);

        when(entityType.getDeclaredId(Long.class)).thenReturn(attribute);
        when(attribute.getName()).thenReturn("id");
        when(type.getJavaType()).thenReturn(Long.class);
        
		interceptor.intercept(stack, method, instance);

		verify(request, never()).setAttribute(eq("entity"), any());
		verify(result).notFound();
		verify(stack, never()).next(method, instance);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentIfEntityDoesntHaveId() throws Exception {
		when(provider.parameterNamesFor(noId.getMethod())).thenReturn(new String[] {"entity"});
		when(request.getParameter("entity.id")).thenReturn("123");
		
        when(entityType.getIdType()).thenReturn(null);
        
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
		
        when(entityType.getDeclaredId(Long.class)).thenReturn(attribute);
        when(attribute.getName()).thenReturn("id");
        when(type.getJavaType()).thenReturn(Long.class);
        
		fail().when(request).setAttribute(eq("entity"), any());
		fail().when(result).notFound();
		fail().when(stack).next(method, instance);

		interceptor.intercept(stack, method, instance);
	}


    static class Entity {
        @Id Long id;
    }
    static class OtherEntity {
        @Id String id;
    }
    static class NoIdEntity {
    }
    
    static class EntityOtherIdName {
        @Id Long otherIdName;
    }
    
    static class Resource {
        public void method(@Load Entity entity) {
        }
        public void other(@Load OtherEntity entity) {
        }
        public void noId(@Load NoIdEntity entity) {
        }
        public void methodOtherIdName(@Load EntityOtherIdName entity) {
        }
    }
    
	private Stubber fail() {
		return doThrow(new AssertionError());
	}
}

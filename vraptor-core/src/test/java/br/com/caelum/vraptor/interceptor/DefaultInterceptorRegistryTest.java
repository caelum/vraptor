package br.com.caelum.vraptor.interceptor;

import static java.util.Arrays.asList;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class DefaultInterceptorRegistryTest {
	
	@Test
	public void shouldRegisterAllComponents() {
		DefaultInterceptorRegistry registry = new DefaultInterceptorRegistry();
		registry.register(ExecuteMethodInterceptor.class, ExceptionHandlerInterceptor.class);
		
		List<Class<? extends Interceptor>> expected = asList(ExecuteMethodInterceptor.class, ExceptionHandlerInterceptor.class);
		Assert.assertEquals(expected, registry.all());
	}
}

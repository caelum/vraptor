package br.com.caelum.vraptor.scan;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;

import org.junit.Test;

public class WebBasedClasspathResolverTest {

	@Test
	public void callerContextDifferentFromRequestedClassLoaderReturnsCurrentClassLoader() {
		ServletContext context = mock(ServletContext.class);
		when(context.getMajorVersion()).thenReturn(3);
		when(context.getClassLoader()).thenThrow(new SecurityException("getClassLoader"));
		WebBasedClasspathResolver resolver = new WebBasedClasspathResolver(context);
		
		assertEquals(resolver.getClassLoader(), Thread.currentThread().getContextClassLoader());
	}

}

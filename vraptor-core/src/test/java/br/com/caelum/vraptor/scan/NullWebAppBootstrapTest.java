package br.com.caelum.vraptor.scan;

import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.mockito.Mockito;

import br.com.caelum.vraptor.ComponentRegistry;

public class NullWebAppBootstrapTest {
	
	@Test
	public void shouldNotDoAnything() {
        ComponentRegistry registry = mock(ComponentRegistry.class);
        new NullWebAppBootstrap().configure(registry);
        Mockito.verifyZeroInteractions(registry);
	}

}

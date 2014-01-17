package br.com.caelum.vraptor.scan;

import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.ComponentRegistry;

public class NullWebAppBootstrapTest {
	
	private @Mock ComponentRegistry registry;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldNotDoAnything() {
	new NullWebAppBootstrap().configure(registry);
	verifyZeroInteractions(registry);
	}
}

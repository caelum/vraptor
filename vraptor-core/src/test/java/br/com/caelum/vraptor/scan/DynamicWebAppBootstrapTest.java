package br.com.caelum.vraptor.scan;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.ComponentRegistry;

public class DynamicWebAppBootstrapTest {
	
    private @Mock ComponentRegistry registry;
    
    @Before
    public void setup() throws Exception {
    	MockitoAnnotations.initMocks(this);
    }
    
	@Test
	public void shouldConfigureAllClasses() {
	    Collection<String> classNames = asList(ResourceA.class.getName());
		new DynamicWebAppBootstrap(classNames).configure(registry);
		
		verify(registry).deepRegister(ResourceA.class);
	}
	
	@Test
	public void shouldNotDoAnythingIfHasNoClasses() {
	    Collection<String> classNames = emptyList();
		new DynamicWebAppBootstrap(classNames).configure(registry);
		
		verify(registry, never()).deepRegister(any(Class.class));
	}
	
	@Test
	public void shouldThrowScannerExceptionIfClassNotExists() {
	    Collection<String> classNames = asList("a.resource.that.NotExists");
	    
	    try {
	    	new DynamicWebAppBootstrap(classNames).configure(registry);
	    	fail("If a class don't exists, a ScannerException should throw");
	    } catch (ScannerException e) {
		}
	}
}

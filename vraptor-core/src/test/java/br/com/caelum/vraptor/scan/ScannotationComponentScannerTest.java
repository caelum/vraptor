package br.com.caelum.vraptor.scan;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.ioc.fixture.ResourceInTheClasspath;

public class ScannotationComponentScannerTest {

    private @Mock ClasspathResolver classPathResolver;
    
    @Before
    public void setup() throws Exception {
    	MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldScanWEBINFClasses() {
        when(classPathResolver.findBasePackages()).thenReturn(Arrays.asList(""));
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        when(classPathResolver.getClassLoader()).thenReturn(classLoader);
		when(classPathResolver.findWebInfClassesLocation()).thenReturn(classLoader.getResource("br/com/caelum/vraptor/ioc/"));

        ScannotationComponentScanner scanner = new ScannotationComponentScanner();
        assertThat(scanner.scan(classPathResolver), hasItem(ResourceInTheClasspath.class.getName()));
    }
    @Test
    public void shouldScanBasePackages() {
    	ClassLoader classLoader = new URLClassLoader(new URL[] {ScannotationComponentScannerTest.class.getResource("/test-fixture.jar")});
    	when(classPathResolver.findBasePackages()).thenReturn(Arrays.asList("br.com.caelum.vraptor.ioc"));
    	when(classPathResolver.getClassLoader()).thenReturn(classLoader);
		when(classPathResolver.findWebInfClassesLocation()).thenReturn(classLoader.getResource("br/com/caelum/vraptor/test/"));

    	ScannotationComponentScanner scanner = new ScannotationComponentScanner();
    	Collection<String> classes = scanner.scan(classPathResolver);
    	assertThat(classes, hasItem(ResourceInTheClasspath.class.getName()));
    }
}

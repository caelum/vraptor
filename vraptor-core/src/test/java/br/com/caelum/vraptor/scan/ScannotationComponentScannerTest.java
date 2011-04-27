package br.com.caelum.vraptor.scan;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.ioc.fixture.ResourceInTheClasspath;

public class ScannotationComponentScannerTest {

    private @Mock ClasspathResolver classPathResolver;

    @Test
    public void shouldScanWEBINFClasses() {
        MockitoAnnotations.initMocks(this);

        this.classPathResolver = mock(ClasspathResolver.class);
        when(classPathResolver.findBasePackages()).thenReturn(Arrays.asList(""));
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        when(classPathResolver.getClassLoader()).thenReturn(classLoader);
		when(classPathResolver.findWebInfClassesLocation()).thenReturn(classLoader.getResource("br/com/caelum/vraptor/ioc/"));

        ScannotationComponentScanner scanner = new ScannotationComponentScanner();
        assertThat(scanner.scan(this.classPathResolver), hasItem(ResourceInTheClasspath.class.getName()));
    }
    @Test
    public void shouldScanBasePackages() {
    	MockitoAnnotations.initMocks(this);

    	this.classPathResolver = mock(ClasspathResolver.class);
    	ClassLoader classLoader = new URLClassLoader(new URL[] {ScannotationComponentScannerTest.class.getResource("/test-fixture.jar")});
    	when(classPathResolver.findBasePackages()).thenReturn(Arrays.asList("br.com.caelum.vraptor.ioc"));
    	when(classPathResolver.getClassLoader()).thenReturn(classLoader);
		when(classPathResolver.findWebInfClassesLocation()).thenReturn(classLoader.getResource("br/com/caelum/vraptor/test/"));

    	ScannotationComponentScanner scanner = new ScannotationComponentScanner();
    	Collection<String> classes = scanner.scan(this.classPathResolver);
    	assertThat(classes, hasItem(ResourceInTheClasspath.class.getName()));
    }
}

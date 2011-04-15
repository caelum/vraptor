package br.com.caelum.vraptor.scan;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ScannotationComponentScannerTest {

    private @Mock ClasspathResolver classPathResolver;
    
    @Test
    public void shouldScanPackages() {
        MockitoAnnotations.initMocks(this);
        
        this.classPathResolver = mock(ClasspathResolver.class);
        when(classPathResolver.findBasePackages()).thenReturn(Arrays.asList("br.com.caelum.vraptor.restfulie"));
        when(classPathResolver.findWebInfClassesLocation()).thenReturn(Thread.currentThread().getContextClassLoader().getResource("br/com/caelum/vraptor/ioc/"));
        
        ScannotationComponentScanner scanner = new ScannotationComponentScanner();
        assertFalse(scanner.scan(this.classPathResolver).isEmpty());
    }
}

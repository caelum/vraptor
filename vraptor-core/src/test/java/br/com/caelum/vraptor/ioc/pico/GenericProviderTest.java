package br.com.caelum.vraptor.ioc.pico;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.reflection.CacheBasedTypeCreator;
import br.com.caelum.vraptor.resource.ResourceRegistry;

/**
 * Should be implemented in a way that tests all provided providers.
 * 
 * @author Guilherme Silveira
 * 
 */
public class GenericProviderTest {

    private PicoProvider provider;
    private Mockery mockery;
    private VRaptorRequest request;

    @Before
    public void setup() throws IOException {
        this.provider = new PicoProvider();
        this.mockery = new Mockery();
        final File tmpDir = File.createTempFile("tmp_", "_file").getParentFile();
        final File tmp = new File(tmpDir, "_tmp_vraptor_test");
        tmp.mkdir();
        final ServletContext context = mockery.mock(ServletContext.class);
        mockery.checking(new Expectations() {
            {
                one(context).getRealPath(""); will(returnValue(tmp.getAbsolutePath()));
            }
        });
        this.provider.start(context);
        HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        HttpServletResponse response = mockery.mock(HttpServletResponse.class);
        this.request = new VRaptorRequest(context, request, response);
    }

    @Test
    public void canProvideAllComponents() {
        Class<?>[] components = new Class[]{UrlToResourceTranslator.class, ResourceRegistry.class, DirScanner.class, ResourceLocator.class, CacheBasedTypeCreator.class, InterceptorListPriorToExecutionExtractor.class,InterceptorRegistry.class};
        for (Class<?> component : components) {
            MatcherAssert.assertThat(canProvide(component), Matchers.is(Matchers.equalTo(true)));
        }
        mockery.assertIsSatisfied();
    }

    private Boolean canProvide(Class<?> type) {
        return provider.instanceFor(type) != null;
    }

}

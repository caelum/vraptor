package br.com.caelum.vraptor.ioc;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.DefaultInterceptorStack;
import br.com.caelum.vraptor.core.DefaultResult;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.http.OgnlParametersProvider;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.ioc.pico.DirScanner;
import br.com.caelum.vraptor.ioc.pico.ResourceLocator;
import br.com.caelum.vraptor.ioc.spring.SpringProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.ResourceRegistry;
import br.com.caelum.vraptor.view.jsp.PageResult;

/**
 * Acceptance test that checks if the container is capable of giving all
 * required components.
 * 
 * @author Guilherme Silveira
 */
public abstract class GenericContainerTest {

    protected Mockery mockery;

    private Container container;

    private ContainerProvider provider;

    private VRaptorRequest request;

    protected ServletContext context;

    protected abstract ContainerProvider getProvider();

    @Before
    public void setup() throws IOException {
        this.mockery = new Mockery();
        final File tmpDir = File.createTempFile("tmp_", "_file").getParentFile();
        final File tmp = new File(tmpDir, "_tmp_vraptor_test");
        tmp.mkdir();
        this.context = mockery.mock(ServletContext.class);
        mockery.checking(new Expectations() {
            {
                one(context).getRealPath("");
                will(returnValue(tmp.getAbsolutePath()));
                // UGLY! move to Spring implementation...
                allowing(context).getInitParameter(SpringProvider.BASE_PACKAGES_PARAMETER_NAME);
                will(returnValue("no.packages"));
            }
        });
        HttpServletRequest request = mockery.mock(HttpServletRequest.class);
        HttpServletResponse response = mockery.mock(HttpServletResponse.class);
        this.request = new VRaptorRequest(context, request, response);
        provider = getProvider();
        provider.start(context);
        this.container = provider.provide(this.request);
    }

    @After
    public void tearDown() {
        provider.stop();
    }

    @Test
    public void canProvideAllRequestScopedComponents() {
        check(HttpServletRequest.class, HttpServletResponse.class,
                VRaptorRequest.class, DefaultInterceptorStack.class, RequestExecution.class,
                ResourceLookupInterceptor.class, InstantiateInterceptor.class, DefaultResult.class,
                ExecuteMethodInterceptor.class, OgnlParametersProvider.class, Converters.class);
        container.register(mockery.mock(ResourceMethod.class));
        check(PageResult.class);
        mockery.assertIsSatisfied();
    }

    private void check(Class<?> ...  components) {
        for (Class<?> component : components) {
            MatcherAssert.assertThat("Should be able to give me a " + component.getName(),
                    container.instanceFor(component), Matchers.is(Matchers.notNullValue()));
        }
    }

    @Test
    public void canProvideAllApplicationScopedComponents() {
        Class<?>[] components = new Class[] { UrlToResourceTranslator.class, ResourceRegistry.class, DirScanner.class,
                ResourceLocator.class, TypeCreator.class, InterceptorRegistry.class };
        check(components);
        mockery.assertIsSatisfied();
    }

}

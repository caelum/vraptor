package br.com.caelum.vraptor.ioc;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

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

    private int counter;
    
    protected Mockery mockery;

    private ContainerProvider provider;

    protected ServletContext context;

    protected abstract ContainerProvider getProvider();

    @Before
    public void setup() throws IOException {
        counter = 0;
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
        createRequest();
        provider = getProvider();
        provider.start(context);
    }

    private VRaptorRequest createRequest() {
        HttpServletRequest request = mockery.mock(HttpServletRequest.class, "req" + counter++);
        HttpServletResponse response = mockery.mock(HttpServletResponse.class, "res" + counter++);
        return new VRaptorRequest(context, request, response);
    }

    @After
    public void tearDown() {
        provider.stop();
    }

    @Test
    public void canProvideAllRequestScopedComponents() {
        checkAvailabilityFor(false, HttpServletRequest.class, HttpServletResponse.class,
                VRaptorRequest.class, DefaultInterceptorStack.class, RequestExecution.class,
                ResourceLookupInterceptor.class, InstantiateInterceptor.class, DefaultResult.class,
                ExecuteMethodInterceptor.class, OgnlParametersProvider.class, Converters.class);
        checkAvailabilityFor(false, PageResult.class);
        mockery.assertIsSatisfied();
    }

    private void checkAvailabilityFor(boolean shouldBeTheSame, Class<?> ...  components) {
        for (Class<?> component : components) {
            Container firstContainer = provider.provide(createRequest());
            firstContainer.register(mockery.mock(ResourceMethod.class, "rm" + firstContainer));
            Object firstInstance = firstContainer.instanceFor(component);
            Container secondContainer = provider.provide(createRequest());
            secondContainer.register(mockery.mock(ResourceMethod.class, "rm" + secondContainer ));
            Object secondInstance = secondContainer.instanceFor(component);
            if(shouldBeTheSame) {
                MatcherAssert.assertThat("Should be the same instance for " + component.getName(),
                        firstInstance, Matchers.is(equalTo(secondInstance)));
            } else {
                MatcherAssert.assertThat("Should not be the same instance for " + component.getName(),
                        firstInstance, Matchers.is(not(equalTo(secondInstance))));
            }
        }
    }

    @Test
    public void canProvideAllApplicationScopedComponents() {
        Class<?>[] components = new Class[] { UrlToResourceTranslator.class, ResourceRegistry.class, DirScanner.class,
                ResourceLocator.class, TypeCreator.class, InterceptorRegistry.class };
        checkAvailabilityFor(true, components);
        mockery.assertIsSatisfied();
    }

}

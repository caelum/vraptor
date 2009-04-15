package br.com.caelum.vraptor.ioc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.OgnlParametersProvider;
import br.com.caelum.vraptor.http.ParameterNameProvider;
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
import br.com.caelum.vraptor.view.PathResolver;
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
        final File unknownFile = new File(tmp, "unknown");
        this.context = mockery.mock(ServletContext.class);
        mockery.checking(new Expectations() {
            {
                allowing(context).getRealPath("");
                will(returnValue(tmp.getAbsolutePath()));
                allowing(context).getRealPath("/WEB-INF/classes/vraptor.xml");
                will(returnValue(unknownFile.getAbsolutePath()));
                allowing(context).getRealPath("/WEB-INF/classes/views.properties");
                will(returnValue(unknownFile.getAbsolutePath()));
                // UGLY! move to Spring implementation...
                allowing(context).getInitParameter(SpringProvider.BASE_PACKAGES_PARAMETER_NAME);
                will(returnValue("no.packages"));
            }
        });
        createRequest();
        provider = getProvider();
        provider.start(context);
    }

    protected VRaptorRequest createRequest() {
        final HttpServletRequest request = mockery.mock(HttpServletRequest.class, "req" + counter++);
        mockery.checking(new Expectations() {
            {
                allowing(request).getSession(); will(returnValue(mockery.mock(HttpSession.class, "session" + counter++)));
                allowing(request).getParameterMap(); will(returnValue(new HashMap<String, String>()));
            }
        });
        HttpServletResponse response = mockery.mock(HttpServletResponse.class, "res" + counter++);
        return new VRaptorRequest(context, request, response);
    }

    @After
    public void tearDown() {
        provider.stop();
    }

    @Test
    public void canProvideAllRequestScopedComponents() {
        checkAvailabilityFor(false, HttpServletRequest.class, HttpServletResponse.class, VRaptorRequest.class,
                DefaultInterceptorStack.class, RequestInfo.class, RequestExecution.class, ResourceLookupInterceptor.class,
                InstantiateInterceptor.class, DefaultResult.class, ExecuteMethodInterceptor.class,
                OgnlParametersProvider.class, Converters.class, HttpSession.class);
        checkAvailabilityFor(false, PageResult.class);
        mockery.assertIsSatisfied();
    }

    private void checkAvailabilityFor(boolean shouldBeTheSame, Class<?> component, Class<?> componentToRegister) {
        Container firstContainer = provider.provide(createRequest());
        Container secondContainer = provider.provide(createRequest());

        firstContainer.register(mockery.mock(ResourceMethod.class, "rm" + counter++));
        secondContainer.register(mockery.mock(ResourceMethod.class, "rm" + counter++));

        if (componentToRegister != null) {
            firstContainer.register(componentToRegister);
            if (secondContainer.instanceFor(componentToRegister) == null) {
                // not an app scoped... then register on the other one too
                secondContainer.register(componentToRegister);
            }
        }

        Object firstInstance = firstContainer.instanceFor(component);
        Object secondInstance = secondContainer.instanceFor(component);
        checkSimilarity(component, shouldBeTheSame, firstInstance, secondInstance);
    }

    private void checkSimilarity(Class<?> component, boolean shouldBeTheSame, Object firstInstance,
            Object secondInstance) {
        if (shouldBeTheSame) {
            MatcherAssert.assertThat("Should be the same instance for " + component.getName(), firstInstance, Matchers
                    .is(equalTo(secondInstance)));
        } else {
            MatcherAssert.assertThat("Should not be the same instance for " + component.getName(), firstInstance,
                    Matchers.is(not(equalTo(secondInstance))));
        }
    }

    @Test
    public void canProvideAllApplicationScopedComponents() {
        Class<?>[] components = new Class[] { UrlToResourceTranslator.class, ResourceRegistry.class, DirScanner.class,
                ResourceLocator.class, TypeCreator.class, InterceptorRegistry.class, PathResolver.class, ParameterNameProvider.class };
        checkAvailabilityFor(true, components);
        mockery.assertIsSatisfied();
    }

    private void checkAvailabilityFor(boolean shouldBeTheSame, Class<?>... components) {
        for (Class<?> component : components) {
            checkAvailabilityFor(shouldBeTheSame, component, null);
        }
    }

    @ApplicationScoped
    public static class MyAppComponent {

    }

    @Test
    public void processesCorrectlyAppBasedComponents() {
        checkAvailabilityFor(true, MyAppComponent.class, MyAppComponent.class);
        mockery.assertIsSatisfied();
    }

    public static class MyRequestComponent {

    }

    @Test
    public void processesCorrectlyRequestBasedComponents() {
        checkAvailabilityFor(false, MyRequestComponent.class, MyRequestComponent.class);
        mockery.assertIsSatisfied();
    }

}

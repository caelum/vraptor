package br.com.caelum.vraptor.ioc;

import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.DefaultInterceptorStack;
import br.com.caelum.vraptor.core.DefaultResult;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.http.OgnlParametersProvider;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.ResourceRegistry;
import br.com.caelum.vraptor.view.PathResolver;
import br.com.caelum.vraptor.view.jsp.PageResult;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

/**
 * Acceptance test that checks if the container is capable of giving all
 * required components.
 *
 * @author Guilherme Silveira
 */
@Ignore
public abstract class GenericContainerTest {

    protected Mockery mockery;

    private ContainerProvider provider;

    protected ServletContext context;

    protected abstract ContainerProvider getProvider();

    protected abstract <T> T executeInsideRequest(Execution<T> execution);

    @Before
    public void setup() throws IOException {
        this.mockery = new Mockery();
        this.context = mockery.mock(ServletContext.class);
        final File tmpDir = File.createTempFile("tmp_", "_file").getParentFile();
        final File tmp = new File(tmpDir, "_tmp_vraptor_test");
        tmp.mkdir();
        mockery.checking(new Expectations() {
            {
                // argh, should be in Pico specific tests
                allowing(context).getRealPath("");
                will(returnValue(tmp.getAbsolutePath()));
            }
        });
        provider = getProvider();
        provider.start(context);
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
                OgnlParametersProvider.class, Converters.class, HttpSession.class, PageResult.class);
        mockery.assertIsSatisfied();
    }

    private <T> void checkAvailabilityFor(final boolean shouldBeTheSame, final Class<T> component,
            final Class<? super T> componentToRegister) {

        T firstInstance = executeInsideRequest(new Execution<T>() {
            public T execute(VRaptorRequest request, int counter) {
                Container firstContainer = provider.provide(request);
                if (componentToRegister != null) {
                    firstContainer.register(componentToRegister);
                }
                ResourceMethod firstMethod = mockery.mock(ResourceMethod.class, "rm" + counter);
                firstContainer.instanceFor(RequestInfo.class).setResourceMethod(firstMethod);
                return firstContainer.instanceFor(component);
            }
        });

        T secondInstance = executeInsideRequest(new Execution<T>() {
            public T execute(VRaptorRequest request, int counter) {
                Container secondContainer = provider.provide(request);

                if (componentToRegister != null && !isAppScoped(secondContainer, componentToRegister)) {
                    secondContainer.register(componentToRegister);
                }

                ResourceMethod secondMethod = mockery.mock(ResourceMethod.class, "rm" + counter);
                secondContainer.instanceFor(RequestInfo.class).setResourceMethod(secondMethod);
                return secondContainer.instanceFor(component);
            }
        });

        checkSimilarity(component, shouldBeTheSame, firstInstance, secondInstance);
    }

    private boolean isAppScoped(Container secondContainer, Class<?> componentToRegister) {
        return secondContainer.instanceFor(componentToRegister) != null;
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
        Class<?>[] components = new Class[]{UrlToResourceTranslator.class, ResourceRegistry.class, TypeCreator.class,
                InterceptorRegistry.class, PathResolver.class, ParameterNameProvider.class};
        checkAvailabilityFor(true, components);
        mockery.assertIsSatisfied();
    }

    protected void checkAvailabilityFor(boolean shouldBeTheSame, Class<?>... components) {
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

    @Component
    public static class MyRequestComponent {

    }

    @Test
    public void processesCorrectlyRequestBasedComponents() {
        checkAvailabilityFor(false, MyRequestComponent.class, MyRequestComponent.class);
        mockery.assertIsSatisfied();
    }

}

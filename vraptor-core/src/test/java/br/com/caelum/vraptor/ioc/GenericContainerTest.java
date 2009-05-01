package br.com.caelum.vraptor.ioc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.ForwardToDefaultViewInterceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.core.URLParameterExtractorInterceptor;
import br.com.caelum.vraptor.http.EmptyElementsRemoval;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.http.RequestParameters;
import br.com.caelum.vraptor.http.Router;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.resource.MethodLookupBuilder;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.ResourceNotFoundHandler;
import br.com.caelum.vraptor.view.LogicResult;
import br.com.caelum.vraptor.view.PathResolver;
import br.com.caelum.vraptor.view.jsp.PageResult;
import br.com.caelum.vraptor.vraptor2.RequestResult;

/**
 * Acceptance test that checks if the container is capable of giving all
 * required components.
 * 
 * @author Guilherme Silveira
 */
@Ignore
public abstract class GenericContainerTest {

    protected Mockery mockery;

    protected ContainerProvider provider;

    protected ServletContext context;

    protected abstract ContainerProvider getProvider();

    protected abstract <T> T executeInsideRequest(WhatToDo<T> execution);

    protected abstract void configureExpectations();

    @Test
    public void canProvideAllApplicationScopedComponents() {
        Class<?>[] components = new Class[] { ServletContext.class, UrlToResourceTranslator.class,
                Router.class, TypeCreator.class, InterceptorRegistry.class, MethodLookupBuilder.class,
                ParameterNameProvider.class, Converters.class, EmptyElementsRemoval.class };
        checkAvailabilityFor(true, components);
        mockery.assertIsSatisfied();
    }

    @Test
    public void canProvideAllRequestScopedComponents() {
        checkAvailabilityFor(false, HttpServletRequest.class, HttpServletResponse.class, RequestInfo.class,
                HttpSession.class, ParametersInstantiatorInterceptor.class, MethodInfo.class,
                RequestParameters.class, InterceptorListPriorToExecutionExtractor.class,
                URLParameterExtractorInterceptor.class, InterceptorStack.class, RequestExecution.class,
                ResourceLookupInterceptor.class, InstantiateInterceptor.class, Result.class,
                ExecuteMethodInterceptor.class, PageResult.class, ParametersProvider.class, MethodInfo.class,
                Validator.class, PathResolver.class, ForwardToDefaultViewInterceptor.class, LogicResult.class,
                RequestResult.class, ResourceNotFoundHandler.class);
        mockery.assertIsSatisfied();
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

    @Before
    public void setup() throws IOException {
        this.mockery = new Mockery();
        this.context = mockery.mock(ServletContext.class, "servlet context");
        configureExpectations();
        provider = getProvider();
        provider.start(context);
    }

    @After
    public void tearDown() {
        if (provider != null) {
            provider.stop();
        }
    }

    private <T> void checkAvailabilityFor(final boolean shouldBeTheSame, final Class<T> component,
            final Class<? super T> componentToRegister) {

        T firstInstance = executeInsideRequest(new WhatToDo<T>() {
            public T execute(RequestInfo request, final int counter) {

                return provider.provideForRequest(request, new Execution<T>() {
                    public T insideRequest(Container firstContainer) {
                        if (componentToRegister != null) {
                            ComponentRegistry registry = firstContainer.instanceFor(ComponentRegistry.class);
                            registry.register(componentToRegister, componentToRegister);
                        }
                        ResourceMethod firstMethod = mockery.mock(ResourceMethod.class, "rm" + counter);
                        firstContainer.instanceFor(MethodInfo.class).setResourceMethod(firstMethod);
                        return firstContainer.instanceFor(component);
                    }
                });

            }
        });

        T secondInstance = executeInsideRequest(new WhatToDo<T>() {
            public T execute(RequestInfo request, final int counter) {

                return provider.provideForRequest(request, new Execution<T>() {
                    public T insideRequest(Container secondContainer) {
                        if (componentToRegister != null && !isAppScoped(secondContainer, componentToRegister)) {
                            ComponentRegistry registry = secondContainer.instanceFor(ComponentRegistry.class);
                            registry.register(componentToRegister, componentToRegister);
                        }

                        ResourceMethod secondMethod = mockery.mock(ResourceMethod.class, "rm" + counter);
                        secondContainer.instanceFor(MethodInfo.class).setResourceMethod(secondMethod);
                        return secondContainer.instanceFor(component);
                    }
                });

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

    protected void checkAvailabilityFor(boolean shouldBeTheSame, Class<?>... components) {
        for (Class<?> component : components) {
            checkAvailabilityFor(shouldBeTheSame, component, null);
        }
    }

}

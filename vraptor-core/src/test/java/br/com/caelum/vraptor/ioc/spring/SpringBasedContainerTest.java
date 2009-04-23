package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.http.UrlToResourceTranslator;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.spring.components.ConstructorInjection;
import br.com.caelum.vraptor.ioc.spring.components.CustomTranslator;
import br.com.caelum.vraptor.ioc.spring.components.DummyComponent;
import br.com.caelum.vraptor.ioc.spring.components.DummyImplementation;
import br.com.caelum.vraptor.ioc.spring.components.RequestScopedComponent;
import br.com.caelum.vraptor.ioc.spring.components.RequestScopedContract;
import br.com.caelum.vraptor.ioc.spring.components.SpecialImplementation;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.hamcrest.Matchers;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Fabio Kung
 */
public class SpringBasedContainerTest {
    private SpringBasedContainer container;
    private Mockery mockery;
    private HttpServletRequestMock request;
    private HttpSessionMock session;
    private ServletContext servletContext;
    private HttpServletResponse response;

    @Before
    public void initContainer() {
        mockery = new Mockery();
        servletContext = mockery.mock(ServletContext.class);
        session = new HttpSessionMock(servletContext, "session");
        request = new HttpServletRequestMock(session);
        response = mockery.mock(HttpServletResponse.class);

        VRaptorRequestHolder.setRequestForCurrentThread(new VRaptorRequest(servletContext, request, response));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        container = new SpringBasedContainer("br.com.caelum.vraptor.ioc.spring");
        container.start(servletContext);
    }

    @After
    public void destroyContainer() {
        container.stop();
        container = null;
        RequestContextHolder.resetRequestAttributes();
        VRaptorRequestHolder.resetRequestForCurrentThread();
    }

    @Test
    public void shouldScanAndRegisterAnnotatedBeans() {
        DummyComponent component = container.instanceFor(DummyComponent.class);
        assertNotNull("can instantiate", component);
        assertTrue("is the right implementation", component instanceof DummyImplementation);
    }

    @Test
    public void shouldSupportOtherStereotypeAnnotations() {
        SpecialImplementation component = container.instanceFor(SpecialImplementation.class);
        assertNotNull("can instantiate", component);
    }

    @Test
    public void shouldSupportConstructorInjection() {
        ConstructorInjection component = container.instanceFor(ConstructorInjection.class);
        assertNotNull("can instantiate", component);
        assertNotNull("inject dependencies", component.getDependecy());
    }

    @Test
    public void shouldProvideCurrentHttpRequest() {
        ServletRequest httpRequest = container.instanceFor(ServletRequest.class);
        assertNotNull("can provide request", httpRequest);
    }

    @Test
    public void shouldProvideCurrentVRaptorRequest() {
        VRaptorRequest vraptorRequest = container.instanceFor(VRaptorRequest.class);
        assertNotNull("can provide request", vraptorRequest);
    }

    @Test
    public void shouldProvideServletContext() {
        ServletContext context = container.instanceFor(ServletContext.class);
        assertNotNull("can provide ServletContext", context);
    }

    @Test
    public void shouldProvideTheContainer() {
        Container itself = this.container.instanceFor(Container.class);
        assertNotNull("can provide the container", itself);
    }

    @Test
    public void shouldSupportManualRegistration() {
        this.container.register(RequestScopedContract.class, RequestScopedComponent.class);
        RequestScopedContract requestScopedContract = this.container.instanceFor(RequestScopedContract.class);
        assertNotNull("can provide manual registered components", requestScopedContract);
    }

    @Test
    public void shoudSupportCustomImplementationsForAlreadyRegisteredComponents() {
        this.container.register(UrlToResourceTranslator.class, CustomTranslator.class);
        UrlToResourceTranslator translator = this.container.instanceFor(UrlToResourceTranslator.class);
        assertThat(translator, is(notNullValue()));
        assertThat(translator, is(instanceOf(CustomTranslator.class)));
    }
}

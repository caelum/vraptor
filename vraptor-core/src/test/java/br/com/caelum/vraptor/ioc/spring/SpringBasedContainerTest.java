package br.com.caelum.vraptor.ioc.spring;

import org.jmock.Mockery;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.ioc.spring.components.ConstructorInjection;
import br.com.caelum.vraptor.ioc.spring.components.DummyComponent;
import br.com.caelum.vraptor.ioc.spring.components.DummyImplementation;
import br.com.caelum.vraptor.ioc.spring.components.SpecialImplementation;
import br.com.caelum.vraptor.test.HttpServletRequestMock;

/**
 * @author Fabio Kung
 */
public class SpringBasedContainerTest {
    private SpringBasedContainer container;
    private Mockery mockery;

    @Before
    public void initContainer() {
        mockery = new Mockery();
//        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new HttpServletRequestMock()));
        container = new SpringBasedContainer("br.com.caelum.vraptor.ioc.spring");
        container.start(mockery.mock(ServletContext.class));
    }

    @After
    public void destroyContainer() {
        container.stop();
        container = null;
        RequestContextHolder.resetRequestAttributes();
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
}

package br.com.caelum.vraptor.ioc.spring;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import static org.junit.Assert.*;
import org.jmock.Mockery;

import javax.servlet.ServletContext;

/**
 * @author Fabio Kung
 */
public class SpringBasedContainerTest {
    private SpringBasedContainer container;
    private Mockery mockery;

    @Before
    public void initContainer() {
        mockery = new Mockery();
        ServletContext servletContext = mockery.mock(ServletContext.class);
        container = new SpringBasedContainer(servletContext, "br.com.caelum.vraptor.ioc.spring");
        container.start();
    }

    public void destroyContainer() {
        container.stop();
        container = null;
        mockery.assertIsSatisfied();
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

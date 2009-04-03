package br.com.caelum.vraptor.ioc.spring;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Fabio Kung
 */
public class SpringBasedContainerTest {
    private SpringBasedContainer container;

    @Before
    public void initContainer() {
        container = new SpringBasedContainer("br.com.caelum.vraptor.ioc.spring");
        container.start(null);
    }

    public void destroyContainer() {
        container.stop();
        container = null;
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

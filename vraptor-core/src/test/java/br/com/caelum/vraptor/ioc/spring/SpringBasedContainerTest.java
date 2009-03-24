package br.com.caelum.vraptor.ioc.spring;

import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.*;

/**
 * @author Fabio Kung
 */
public class SpringBasedContainerTest {
    @Test
    public void shouldScanAndRegisterAnnotatedBeans() {
        SpringBasedContainer container = new SpringBasedContainer("br.com.caelum.vraptor.ioc.spring");
        container.start();
        DummyComponent component = container.instanceFor(DummyComponent.class);
        assertNotNull("can instantiate", component);
        assertTrue("is the right implementation", component instanceof DummyImplementation);
        container.stop();
    }

    public void shouldSupportOtherStereotypeAnnotations() {
        SpringBasedContainer container = new SpringBasedContainer("br.com.caelum.vraptor.ioc.spring");
        container.start();
        SpecialImplementation component = container.instanceFor(SpecialImplementation.class);
        assertNotNull("can instantiate", component);
        container.stop();
    }
}

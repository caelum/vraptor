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
        SpringBasedContainer container = new SpringBasedContainer();
        container.start();
        DummyComponent component = container.instanceFor(DummyComponent.class);
        assertNotNull("can instantiate", component);
        assertTrue("is the right implementation", component instanceof DummyImplementation);
        container.stop();
    }
}

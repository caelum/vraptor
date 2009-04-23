package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.resource.DefaultResource;
import br.com.caelum.vraptor.resource.ResourceRegistry;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class ResourceAcceptorTest {

    private ResourceAcceptor acceptor;
    private Mockery mockery;
    private ResourceRegistry registry;

    @Before
    public void setup() {
        mockery = new Mockery();
        registry = mockery.mock(ResourceRegistry.class);
        this.acceptor = new ResourceAcceptor(registry);
    }

    @Test
    public void shouldAcceptResourcesAnnotatedWithResourceAnnotation() {
        mockery.checking(new Expectations() {
            {
                one(registry).register(new DefaultResource(ResourceAnnotated.class));
            }
        });
        acceptor.analyze(ResourceAnnotated.class);
        mockery.assertIsSatisfied();
    }

    @Test
    public void ignoresNonAnnotatedResources() {
        acceptor.analyze(ResourceNotAnnotated.class);
        mockery.assertIsSatisfied();
    }

    @Resource
    class ResourceAnnotated {
    }

    class ResourceNotAnnotated {
    }

}

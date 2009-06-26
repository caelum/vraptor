package br.com.caelum.vraptor.ioc.pico;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.resource.DefaultStereotypedClass;

public class ResourceAcceptorTest {

    private StereotypedClassAcceptor acceptor;
    private Mockery mockery;
    private Router registry;

    @Before
    public void setup() {
        mockery = new Mockery();
        registry = mockery.mock(Router.class);
        this.acceptor = new StereotypedClassAcceptor(registry);
    }

    @Test
    public void shouldAcceptResourcesAnnotatedWithResourceAnnotation() {
        mockery.checking(new Expectations() {
            {
                one(registry).register(new DefaultStereotypedClass(ResourceAnnotated.class));
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

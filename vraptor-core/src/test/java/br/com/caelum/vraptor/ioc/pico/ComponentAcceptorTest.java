package br.com.caelum.vraptor.ioc.pico;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.resource.DefaultResourceClass;

public class ComponentAcceptorTest {

    private ComponentAcceptor acceptor;
    private Mockery mockery;
    private ComponentRegistry registry;

    @Before
    public void setup() {
        mockery = new Mockery();
        registry = mockery.mock(ComponentRegistry.class);
        this.acceptor = new ComponentAcceptor(registry);
    }

    @Test
    public void shouldAcceptComponentsAnnotatedWithComponentAnnotation() {
        mockery.checking(new Expectations() {
            {
                one(registry).register(ComponentAnnotated.class, ComponentAnnotated.class);
            }
        });
        acceptor.analyze(ComponentAnnotated.class);
        mockery.assertIsSatisfied();
    }

    @Test
    public void ignoresNonAnnotatedComponents() {
        acceptor.analyze(ComponentNotAnnotated.class);
        mockery.assertIsSatisfied();
    }

    @Resource
    class ComponentNotAnnotated {
    }

    @Component
    class ComponentAnnotated {
    }

}

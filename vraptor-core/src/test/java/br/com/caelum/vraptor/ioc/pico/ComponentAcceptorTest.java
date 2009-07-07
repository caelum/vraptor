package br.com.caelum.vraptor.ioc.pico;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.ioc.Component;

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
    public void shouldNotAcceptComponentsAnnotatedWithResourceAnnotation() {
        mockery.checking(new Expectations());
        acceptor.analyze(ResourceAnnotated.class);
        mockery.assertIsSatisfied();
    }

    @Test
    public void ignoresNonAnnotatedComponents() {
        acceptor.analyze(ComponentNotAnnotated.class);
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void shouldRegisterComponentUsingDefinedInterface() {
        mockery.checking(new Expectations(){{
            one(registry).register(Runnable.class, RunnableComponent.class);
            one(registry).register(RunnableComponent.class, RunnableComponent.class);
        }});
        acceptor.analyze(RunnableComponent.class);
        mockery.assertIsSatisfied();
    }
    
    public void shouldRegisterForLotsOfInterfacesWhenArrayListAccepted() {
        mockery.checking(new Expectations(){{
        	
        	
            allowing(registry).register(ArrayListSubclass.class, ArrayListSubclass.class);
            allowing(registry).register(ArrayList.class, ArrayListSubclass.class);
            allowing(registry).register(List.class, ArrayListSubclass.class);
            allowing(registry).register(Collection.class, ArrayListSubclass.class);
            allowing(registry).register(Iterable.class, ArrayListSubclass.class);
            allowing(registry).register(Cloneable.class, ArrayListSubclass.class);
            allowing(registry).register(Serializable.class, ArrayListSubclass.class);
            allowing(registry).register(RandomAccess.class, ArrayListSubclass.class);
            allowing(registry).register(AbstractList.class, ArrayListSubclass.class);
            allowing(registry).register(AbstractCollection.class, ArrayListSubclass.class);
            allowing(registry).register(List.class, ArrayListSubclass.class);
            allowing(registry).register(Collection.class, ArrayListSubclass.class);
        }});
        acceptor.analyze(ArrayListSubclass.class);
    }

    class ComponentNotAnnotated {
    }

    @Resource
    class ResourceAnnotated {
    }

    @Component
    class ComponentAnnotated {
    }

    @Component
    class RunnableComponent implements Runnable {
		public void run() {	}
    }
    
    @SuppressWarnings("serial")
	@Component
    class ArrayListSubclass extends ArrayList<Object> {
    	
    }
}

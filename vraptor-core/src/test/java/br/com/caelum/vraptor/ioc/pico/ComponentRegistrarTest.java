package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.Stereotype;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

/**
 * @author Fabio Kung
 */
public class ComponentRegistrarTest {

    private ComponentRegistrar registrar;
    private Mockery mockery;
    private ComponentRegistry registry;
    private Scanner scanner;

    @Before
    public void setup() {
        mockery = new Mockery();
        registry = mockery.mock(ComponentRegistry.class);
        scanner = mockery.mock(Scanner.class);
        this.registrar = new ComponentRegistrar(registry);
    }

    @Test
    public void shouldRegisterComponentsAnnotatedWithAnyStereotypedAnnotations() {
        mockery.checking(new Expectations() {
            {
                one(scanner).getTypesWithMetaAnnotation(Stereotype.class);
                will(returnValue(Arrays.asList(ComponentAnnotated.class, ResourceAnnotated.class)));
                one(registry).register(ComponentAnnotated.class, ComponentAnnotated.class);
                one(registry).register(ResourceAnnotated.class, ResourceAnnotated.class);
            }
        });
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldRegisterComponentAlsoUsingImplementedInterfaces() {
        mockery.checking(new Expectations() {{
            one(scanner).getTypesWithMetaAnnotation(Stereotype.class);
            will(returnValue(Arrays.asList(RunnableComponent.class)));

            one(registry).register(Runnable.class, RunnableComponent.class);
            one(registry).register(RunnableComponent.class, RunnableComponent.class);
        }});
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldRegisterComponentUsingAllPossibleSupertypes() {
        mockery.checking(new Expectations() {{
            one(scanner).getTypesWithMetaAnnotation(Stereotype.class);
            will(returnValue(Arrays.asList(ArrayListSubclass.class)));

            one(registry).register(ArrayListSubclass.class, ArrayListSubclass.class);
            one(registry).register(ArrayList.class, ArrayListSubclass.class);
            one(registry).register(List.class, ArrayListSubclass.class);
            atLeast(1).of(registry).register(Collection.class, ArrayListSubclass.class);
            atLeast(1).of(registry).register(Iterable.class, ArrayListSubclass.class);
            one(registry).register(Cloneable.class, ArrayListSubclass.class);
            one(registry).register(Serializable.class, ArrayListSubclass.class);
            one(registry).register(RandomAccess.class, ArrayListSubclass.class);
            one(registry).register(AbstractList.class, ArrayListSubclass.class);
            one(registry).register(AbstractCollection.class, ArrayListSubclass.class);
            one(registry).register(List.class, ArrayListSubclass.class);
        }});
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Resource
    class ResourceAnnotated {
    }

    @Component
    class ComponentAnnotated {
    }

    @Component
    class RunnableComponent implements Runnable {
        public void run() {
        }
    }

    @SuppressWarnings("serial")
    @Component
    class ArrayListSubclass extends ArrayList<Object> {

    }
}

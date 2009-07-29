package br.com.caelum.vraptor.ioc.pico;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.core.ComponentHandler;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.Stereotype;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

/**
 * @author Fabio Kung
 */
public class StereotypedComponentRegistrarTest {

    private StereotypedComponentRegistrar registrar;
    private Mockery mockery;
    private ComponentRegistry registry;
    private Scanner scanner;

    @SuppressWarnings("unchecked")
	@Before
    public void setup() {
        mockery = new Mockery();
        registry = mockery.mock(ComponentRegistry.class);
        scanner = mockery.mock(Scanner.class);
        Arrays.asList(Component.class, Intercepts.class, Convert.class, Resource.class);
        
        this.registrar = new StereotypedComponentRegistrar(registry, Collections.<StereotypeHandler>singletonList(new ComponentHandler()));
    }

    @SuppressWarnings("unchecked")
	@Test
    public void shouldRegisterComponentsAnnotatedWithAnyStereotypedAnnotations() {
        mockery.checking(new Expectations() {
            {
                one(scanner).getTypesWithMetaAnnotation(Stereotype.class);
                will(returnValue(Arrays.asList(ComponentAnnotated.class, ResourceAnnotated.class)));
                one(registry).register(ComponentAnnotated.class, ComponentAnnotated.class);
                one(registry).register(ResourceAnnotated.class, ResourceAnnotated.class);

                allowing(scanner).getTypesWithAnnotation(Component.class);
                allowing(scanner).getTypesWithAnnotation(Resource.class);
                allowing(scanner).getTypesWithAnnotation(Intercepts.class);
                allowing(scanner).getTypesWithAnnotation(Convert.class);
            }
        });
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldRegisterComponentsAnnotatedWithComponent() {
        mockery.checking(new Expectations() {
            {
                one(scanner).getTypesWithAnnotation(Component.class);
                will(returnValue(Collections.singletonList(ComponentAnnotated.class)));
                one(registry).register(ComponentAnnotated.class, ComponentAnnotated.class);

                allowing(scanner).getTypesWithAnnotation(Resource.class);
                allowing(scanner).getTypesWithAnnotation(Intercepts.class);
                allowing(scanner).getTypesWithAnnotation(Convert.class);
                allowing(scanner).getTypesWithMetaAnnotation(Stereotype.class);
            }
        });
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldRegisterComponentAlsoUsingImplementedInterfaces() {
        mockery.checking(new Expectations() {{
            one(scanner).getTypesWithAnnotation(Component.class);
            will(returnValue(Collections.singletonList(RunnableComponent.class)));

            one(registry).register(Runnable.class, RunnableComponent.class);
            one(registry).register(RunnableComponent.class, RunnableComponent.class);

            allowing(scanner).getTypesWithAnnotation(Resource.class);
            allowing(scanner).getTypesWithAnnotation(Intercepts.class);
            allowing(scanner).getTypesWithAnnotation(Convert.class);
            allowing(scanner).getTypesWithMetaAnnotation(Stereotype.class);
        }});
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldRegisterComponentUsingAllPossibleSupertypes() {
        mockery.checking(new Expectations() {{
            one(scanner).getTypesWithAnnotation(Component.class);
            will(returnValue(Collections.singletonList(ArrayListSubclass.class)));

            one(registry).register(ArrayListSubclass.class, ArrayListSubclass.class);
            one(registry).register(ArrayList.class, ArrayListSubclass.class);
            one(registry).register(List.class, ArrayListSubclass.class);
            one(registry).register(Collection.class, ArrayListSubclass.class);
            one(registry).register(Iterable.class, ArrayListSubclass.class);
            one(registry).register(Cloneable.class, ArrayListSubclass.class);
            one(registry).register(Serializable.class, ArrayListSubclass.class);
            one(registry).register(RandomAccess.class, ArrayListSubclass.class);
            one(registry).register(AbstractList.class, ArrayListSubclass.class);
            one(registry).register(AbstractCollection.class, ArrayListSubclass.class);

            allowing(scanner).getTypesWithAnnotation(Resource.class);
            allowing(scanner).getTypesWithAnnotation(Intercepts.class);
            allowing(scanner).getTypesWithAnnotation(Convert.class);
            allowing(scanner).getTypesWithMetaAnnotation(Stereotype.class);
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

    @Convert(String.class)
    class ConvertAnnotated {
    }

    @Intercepts
    class InterceptsAnnotated {
    }
}

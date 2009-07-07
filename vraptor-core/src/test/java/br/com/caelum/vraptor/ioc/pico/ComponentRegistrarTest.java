package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.Stereotype;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

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
                will(returnValue(Arrays.asList(ComponentAnnotated.class)));
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
    public void shouldRegisterComponentsAnnotatedWithResource() {
        mockery.checking(new Expectations() {
            {
                one(scanner).getTypesWithAnnotation(Resource.class);
                will(returnValue(Arrays.asList(ResourceAnnotated.class)));
                one(registry).register(ResourceAnnotated.class, ResourceAnnotated.class);

                allowing(scanner).getTypesWithAnnotation(Component.class);
                allowing(scanner).getTypesWithAnnotation(Intercepts.class);
                allowing(scanner).getTypesWithAnnotation(Convert.class);
                allowing(scanner).getTypesWithMetaAnnotation(Stereotype.class);
            }
        });
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldRegisterComponentsAnnotatedWithConvert() {
        mockery.checking(new Expectations() {
            {
                one(scanner).getTypesWithAnnotation(Convert.class);
                will(returnValue(Arrays.asList(ConvertAnnotated.class)));
                one(registry).register(ConvertAnnotated.class, ConvertAnnotated.class);

                allowing(scanner).getTypesWithAnnotation(Component.class);
                allowing(scanner).getTypesWithAnnotation(Resource.class);
                allowing(scanner).getTypesWithAnnotation(Intercepts.class);
                allowing(scanner).getTypesWithMetaAnnotation(Stereotype.class);
            }
        });
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldRegisterComponentsAnnotatedWithIntercepts() {
        mockery.checking(new Expectations() {
            {
                one(scanner).getTypesWithAnnotation(Intercepts.class);
                will(returnValue(Arrays.asList(InterceptsAnnotated.class)));
                one(registry).register(InterceptsAnnotated.class, InterceptsAnnotated.class);

                allowing(scanner).getTypesWithAnnotation(Component.class);
                allowing(scanner).getTypesWithAnnotation(Resource.class);
                allowing(scanner).getTypesWithAnnotation(Convert.class);
                allowing(scanner).getTypesWithMetaAnnotation(Stereotype.class);
            }
        });
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Test
    @Ignore("deepRegister is no more required")
    public void shouldRegisterComponentAlsoUsingImplementedInterfaces() {
        mockery.checking(new Expectations() {{
            one(scanner).getTypesWithAnnotation(Component.class);
            will(returnValue(Arrays.asList(RunnableComponent.class)));

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
    @Ignore("deepRegister is no more required")
    public void shouldRegisterComponentUsingAllPossibleSupertypes() {
        mockery.checking(new Expectations() {{
            one(scanner).getTypesWithAnnotation(Component.class);
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

package br.com.caelum.vraptor.ioc;

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

/**
 *
 * @author Lucas Cavalcanti
 *
 */
public class AbstractComponentRegistryTest {


	private Mockery mockery;
	private ComponentRegistry registry;
	private AbstractComponentRegistry abstractRegistry;

	@Before
	public void setUp() throws Exception {
		mockery = new Mockery();
		registry = mockery.mock(ComponentRegistry.class);

		abstractRegistry = new AbstractComponentRegistry() {
			public void register(Class<?> requiredType, Class<?> componentType) {
				registry.register(requiredType, componentType);
			}
		};
	}
	@Test
    public void shouldRegisterComponentAndImplementedInterfaces() {
        mockery.checking(new Expectations() {{

            one(registry).register(Runnable.class, RunnableComponent.class);
            one(registry).register(RunnableComponent.class, RunnableComponent.class);

        }});
        abstractRegistry.deepRegister(RunnableComponent.class);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldRegisterComponentUsingAllPossibleSupertypes() {
        mockery.checking(new Expectations() {{

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

        }});
        abstractRegistry.deepRegister(ArrayListSubclass.class);
        mockery.assertIsSatisfied();
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

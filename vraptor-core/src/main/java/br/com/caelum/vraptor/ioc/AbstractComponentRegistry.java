package br.com.caelum.vraptor.ioc;

import java.util.HashSet;
import java.util.Set;

import br.com.caelum.vraptor.ComponentRegistry;

/**
 * An abstract component registry that provides expected behavior for deepRegistry method.
 * @author Lucas Cavalcanti
 *
 */
public abstract class AbstractComponentRegistry implements ComponentRegistry {

	public final void deepRegister(Class<?> componentType) {
		deepRegister(componentType, componentType, new HashSet<Class<?>>());
	}

	private void deepRegister(Class<?> required, Class<?> component, Set<Class<?>> registeredKeys) {
        if (required == null || required.equals(Object.class)) {
			return;
		}

        if (!registeredKeys.contains(required)) {
            registeredKeys.add(required);
            register(required, component);
        }

        for (Class<?> c : required.getInterfaces()) {
            deepRegister(c, component, registeredKeys);
        }

        deepRegister(required.getSuperclass(), component, registeredKeys);
    }

}

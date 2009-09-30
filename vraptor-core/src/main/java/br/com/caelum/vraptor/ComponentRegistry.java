
package br.com.caelum.vraptor;

/**
 * <p>Represents the registry of all components allowing them to be registered for injection.</p>
 * <p>This interface doesnt allow component lookup, so during component
 * registration phase no provider tries to instantiate something before it's time
 * to do so.</p>
 *
 * @author Guilherme Silveira
 */
public interface ComponentRegistry {

    /**
     * Registers a component to be used only when the required type is required.
     * @param componentType the component type
     */
    public void register(Class<?> requiredType, Class<?> componentType);

    /**
     * Registers a component to be used when the required type is the component, or
     * any of their interfaces and superclasses.
     * @param componentType the component type
     */
    public void deepRegister(Class<?> componentType);

}

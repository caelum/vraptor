package br.com.caelum.vraptor.proxy;

/**
 * Responsible for instantiating objects.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.3.1
 */
public interface InstanceCreator {

    /**
     * Creates an instance for a class.
     * 
     * @param clazz The instance class.
     * @return The instance for class.
     */
    <T> T instanceFor(Class<T> clazz);

}

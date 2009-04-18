package br.com.caelum.vraptor;

/**
 * Represents the base of all components allowing them to be registered.<br>
 * This interface doesnt allow component lookup so during the component
 * registration phase no provider tries to instantiate something before its time
 * to do so.
 * 
 * @author Guilherme Silveira
 */
public interface RegisterContainer {

    /**
     * Registers a component in the component registry.
     */
    public void register(Class<?> type);

}

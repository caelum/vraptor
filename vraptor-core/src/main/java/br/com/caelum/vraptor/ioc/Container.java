package br.com.caelum.vraptor.ioc;


public interface Container {

    /**
     * Retrieves the appropriate instance for the given class.
     *
     * @param type of the required component
     * @param <T>
     * @return the registered component
     */
    <T> T instanceFor(Class<T> type);

}

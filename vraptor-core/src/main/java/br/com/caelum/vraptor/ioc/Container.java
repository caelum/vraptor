
package br.com.caelum.vraptor.ioc;

/**
 * Provides components.<br>
 * Registerable components can have different scopes: request (by default),
 * session and application scope (only one instance for the entire app). App
 * scoped components are registered through the use of the annotation
 * \@ApplicatonScope.
 * 
 * @author Guilherme Silveira
 */
public interface Container {

    /**
     * Retrieves the appropriate instance for the given class.
     * 
     * @param type
     *            of the required component
     * @param <T>
     * @return the registered component
     */
    <T> T instanceFor(Class<T> type);

}

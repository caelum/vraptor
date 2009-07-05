package br.com.caelum.vraptor.ioc;

/**
 *
 * @author Fabio Kung
 */
public interface ComponentFactory<T> {
    T getInstance();
}

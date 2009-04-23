package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.ioc.Container;

/**
 * @author Fabio Kung
 */
public interface Execution<T> {
    T insideRequest(Container container);
}

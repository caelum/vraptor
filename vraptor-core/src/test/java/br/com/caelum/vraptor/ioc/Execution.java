package br.com.caelum.vraptor.ioc;

import br.com.caelum.vraptor.core.VRaptorRequest;

/**
 * @author Fabio Kung
 */
public interface Execution<T> {
    T execute(VRaptorRequest request);
}

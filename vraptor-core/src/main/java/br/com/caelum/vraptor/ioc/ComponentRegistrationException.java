package br.com.caelum.vraptor.ioc;

import br.com.caelum.vraptor.VRaptorException;

/**
 * @author: Fabio Kung
 */
public class ComponentRegistrationException extends VRaptorException {
    public ComponentRegistrationException(Throwable e) {
        super(e);
    }

    public ComponentRegistrationException(String msg) {
        super(msg);
    }

    public ComponentRegistrationException(String msg, Throwable e) {
        super(msg, e);
    }
}

package br.com.caelum.vraptor.proxy;

import br.com.caelum.vraptor.VRaptorException;

/**
 * @author Fabio Kung
 */
public class ProxyInvocationException extends VRaptorException {
    private static final long serialVersionUID = 5465881268532840163L;

    public ProxyInvocationException(Throwable e) {
        super(e);
    }

    public ProxyInvocationException(String msg) {
        super(msg);
    }

    public ProxyInvocationException(String msg, Throwable e) {
        super(msg, e);
    }
}

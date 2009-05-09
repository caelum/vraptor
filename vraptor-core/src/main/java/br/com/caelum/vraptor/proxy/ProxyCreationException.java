package br.com.caelum.vraptor.proxy;

import br.com.caelum.vraptor.VRaptorException;

/**
 * @author Fabio Kung
 */
public class ProxyCreationException extends VRaptorException {
    private static final long serialVersionUID = 1587022120133421006L;

    public ProxyCreationException(Throwable e) {
        super(e);
    }

    public ProxyCreationException(String msg) {
        super(msg);
    }

    public ProxyCreationException(String msg, Throwable e) {
        super(msg, e);
    }
}

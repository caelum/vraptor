
package br.com.caelum.vraptor;

public class InterceptionException extends VRaptorException {
    private static final long serialVersionUID = -1964321560573946245L;

    public InterceptionException(Throwable e) {
        super(e);
    }

    public InterceptionException(String msg) {
        super(msg);
    }

    public InterceptionException(String msg, Throwable e) {
        super(msg, e);
    }

}

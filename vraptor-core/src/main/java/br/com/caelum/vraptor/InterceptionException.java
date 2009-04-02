package br.com.caelum.vraptor;

public class InterceptionException extends VRaptorException {

    public InterceptionException(Throwable e) {
        super(e);
    }

    public InterceptionException(String msg) {
        super(msg);
    }

}

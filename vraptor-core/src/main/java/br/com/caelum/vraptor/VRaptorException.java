
package br.com.caelum.vraptor;

public class VRaptorException extends RuntimeException {
    private static final long serialVersionUID = -8040463849613736889L;

    public VRaptorException(Throwable e) {
        super(e);
    }

    public VRaptorException(String msg) {
        super(msg);
    }

    public VRaptorException(String msg, Throwable e) {
        super(msg, e);
    }

}

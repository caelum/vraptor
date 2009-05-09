package br.com.caelum.vraptor.view;

import br.com.caelum.vraptor.VRaptorException;

/**
 * @author Fabio Kung
 */
public class ResultException extends VRaptorException {
    private static final long serialVersionUID = 613016550272361973L;

    public ResultException(Throwable e) {
        super(e);
    }

    public ResultException(String msg) {
        super(msg);
    }

    public ResultException(String msg, Throwable e) {
        super(msg, e);
    }
}

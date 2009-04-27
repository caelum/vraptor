package br.com.caelum.vraptor.view;

import br.com.caelum.vraptor.VRaptorException;

/**
 * @author Fabio Kung
 */
public class ResultException extends VRaptorException {
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

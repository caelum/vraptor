package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.VRaptorException;

public class ExpressionEvaluationException extends VRaptorException {
    private static final long serialVersionUID = -6179625926472054963L;

    public ExpressionEvaluationException(String msg, Throwable e) {
        super(msg, e);
    }

    public ExpressionEvaluationException(Throwable e) {
        super(e);
    }

    public ExpressionEvaluationException(String msg) {
        super(msg);
    }
}
